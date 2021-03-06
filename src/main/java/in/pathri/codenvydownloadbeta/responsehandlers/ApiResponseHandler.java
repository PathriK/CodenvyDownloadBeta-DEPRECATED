package in.pathri.codenvydownloadbeta.responsehandlers;

import java.io.IOException;
import java.util.List;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import in.pathri.codenvydownloadbeta.CustomLogger;
import in.pathri.codenvydownloadbeta.CustomProgressDialog;
import in.pathri.codenvydownloadbeta.HomePageActivity;
import in.pathri.codenvydownloadbeta.pojo.CodenvyResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class ApiResponseHandler < T > implements Callback < T > {
	private static final String className = ApiResponseHandler.class.getSimpleName();
    final static String START = "start";
    final static String END = "end";
  	 boolean retryFlag = false;
    ProgressBar spinner = null;
    CustomProgressDialog pd = null;
    
    public enum ResponseType{
        POJO,BINARY,ARRAY
    }
    public ResponseType responseType = ResponseType.BINARY;
    
    ApiResponseHandler(ProgressBar spinner, Class < ? > clazz) {
        this.spinner = spinner;
        updateProgress(START);
        if (CodenvyResponse.class.equals(clazz)) {
            this.responseType = ResponseType.POJO;
        }
    }

    ApiResponseHandler(CustomProgressDialog pd, Class < ? > clazz){
        this.pd = pd;
        updateProgress(START);
        if (CodenvyResponse.class.equals(clazz)) {
            this.responseType = ResponseType.POJO;
        }
    }

    ApiResponseHandler(CustomProgressDialog pd){
        this.pd = pd;
        updateProgress(START);
        this.responseType = ResponseType.ARRAY;
    }
    
	abstract void updateStatusText(String statusText);
    
    public abstract void nextStep(CodenvyResponse codenvyResponse);
    
    abstract void nextStep(List<CodenvyResponse> codenvyResponse);
    
    abstract void nextStep(ResponseBody responseBody);
  
    abstract void handleAuthIssue(ResponseBody responseBody);
    
    abstract void handleCookie(Response <T> response);
    
    public abstract void onConnect();
    
    @Override
    public void onFailure(Call < T > arg0, Throwable t) {
        this.updateStatusText("Connection Error" + HomePageActivity.getStackTraceString(t));
        updateProgress(END);
    }
    
    @Override
    public void onResponse(Call < T > request, Response < T > response) {
      CustomLogger.d(className, "onResponse", "Request URL:", request.request().url().toString());
        if (response.isSuccessful()) {
            if (200 == response.code()) {
            	this.handleCookie(response);
//            	try {
//					CustomLogger.d(className, "onResponse", "Raw Response", response.raw().body().string());
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
                switch (responseType){
                    case POJO:
                      CodenvyResponse apiResponse = (CodenvyResponse) response.body();
                      if (apiResponse == null) {
                          this.updateStatusText("Empty Response");
                          } else {
                          this.updateStatusText("Success");
                          this.nextStep(apiResponse);
                      }
                      break;
                    case BINARY:
                      ResponseBody apiResponseBinary = (ResponseBody) response.body();
                      if (apiResponseBinary == null) {
                          this.updateStatusText("Empty Response");
                          } else {
                          this.updateStatusText("Success");
                          this.nextStep(apiResponseBinary);
                      }
                      break;
                    case ARRAY:
                      List<CodenvyResponse> apiResponseArr = (List<CodenvyResponse>) response.body();
                      if (apiResponseArr == null) {
                          this.updateStatusText("Empty Response");
                          } else {
                          this.updateStatusText("Success");
                          this.nextStep(apiResponseArr);
                      }
                      break;
                }
            } else {
                this.updateStatusText("Not Success - " + response.code());
            }
        }
        else {
            ResponseBody k = response.errorBody();
            if (k == null) {
                this.updateStatusText("Empty Error Response");
            }else{
            try {
                if(401 == response.code() || 403 == response.code()){
                  if(!retryFlag){
                    retryFlag = true;
                    this.handleAuthIssue(k);
                  }else{
                		this.updateStatusText("Error: " + k.string());    
                  }
                }else{
                  this.updateStatusText("Error: " + k.string());    
                }                
                } catch (Exception e) {
                e.printStackTrace();
            }
            }
        }
        updateProgress(END);
    }
    
    private void updateProgress(String status){
        if(START.equalsIgnoreCase(status)){
            if(spinner != null){
//                this.spinner.setVisibility(View.VISIBLE);
                CustomLogger.i(className, "updateProgress", "Visible");
            }
            if(pd != null){
                pd.show();
            }
            }else{
            if(spinner != null){
                this.spinner.setVisibility(View.GONE);
            }
            if(pd != null){
                pd.dismiss();
            }
        }
    }
}