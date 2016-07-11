package in.pathri.codenvydownloadbeta.responsehandlers;

import java.util.List;

import in.pathri.codenvydownloadbeta.HomePageActivity;
import in.pathri.codenvydownloadbeta.pojo.CodenvyResponse;
import okhttp3.ResponseBody;
import retrofit2.Response;

public class VoidResponseHandler extends ApiResponseHandler<ResponseBody> {
	private static final String className = VoidResponseHandler.class.getSimpleName();

    public VoidResponseHandler(){
        super(HomePageActivity.buildSpinner,ResponseBody.class);
    }
    
    @Override
    void nextStep(ResponseBody arg0) {
        HomePageActivity.updateStatusText("Application Error!!");
    }
  
    @Override
    void handleAuthIssue(ResponseBody responseBody) {
        try{
           this.updateStatusText("Error: " + responseBody.string());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }      
    
    @Override
    void updateStatusText(String statusText) {
        HomePageActivity.updateBuildStatusText(statusText);
    }
    
    @Override
    void nextStep(List<CodenvyResponse> codenvyResponses) {
        HomePageActivity.updateTriggerStatusText("Application Error!!");
    }

	@Override
	public 	void nextStep(CodenvyResponse codenvyResponse) {
		HomePageActivity.updateStatusText("Application Error!!");		
	}

	@Override
	void handleCookie(Response<ResponseBody> response) {
		// TODO Auto-generated method stub
		
	}
}