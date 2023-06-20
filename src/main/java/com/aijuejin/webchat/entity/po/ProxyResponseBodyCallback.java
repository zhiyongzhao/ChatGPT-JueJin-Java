package com.aijuejin.webchat.entity.po;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.OpenAiError;
import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.service.SSEFormatException;
import io.reactivex.FlowableEmitter;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @Description:
 * @Title: ProxySSEPO
 * @Package com.aijuejin.webchat.entity.po
 * @Author: zhaozhiyong
 * @Copyright 版权归**企业（或个人）所有
 * @CreateTime: 2023/5/20 18:44
 */
public class ProxyResponseBodyCallback implements Callback<ResponseBody> {
    private static final ObjectMapper mapper = OpenAiService.defaultObjectMapper();

    private FlowableEmitter<ProxySSEPO> emitter;
    private boolean emitDone;

    public ProxyResponseBodyCallback(FlowableEmitter<ProxySSEPO> emitter, boolean emitDone) {
        this.emitter = emitter;
        this.emitDone = emitDone;
    }

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        BufferedReader reader = null;

        try {
            if (!response.isSuccessful()) {
                HttpException e = new HttpException(response);
                ResponseBody errorBody = response.errorBody();

                if (errorBody == null) {
                    throw e;
                } else {
                    OpenAiError error = mapper.readValue(
                            errorBody.string(),
                            OpenAiError.class
                    );
                    throw new OpenAiHttpException(error, e, e.code());
                }
            }

            InputStream in = response.body().byteStream();
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            ProxySSEPO sse = null;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("data:")) {
                    String data = line.substring(5).trim();
                    sse = new ProxySSEPO(data);
                } else if (line.equals("") && sse != null) {
                    if (sse.isDone()) {
                        if (emitDone) {
                            emitter.onNext(sse);
                        }
                        break;
                    }

                    emitter.onNext(sse);
                    sse = null;
                } else {
                    throw new SSEFormatException("Invalid sse format! " + line);
                }
            }

            emitter.onComplete();

        } catch (Throwable t) {
            onFailure(call, t);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // do nothing
                }
            }
        }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {
        emitter.onError(t);
    }
}