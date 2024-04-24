package com.cinntra.vistadelivery.webservices;


import com.cinntra.vistadelivery.model.AttachmentResponseModel;
import com.cinntra.vistadelivery.model.BPModel.AddBranchRequestModel;
import com.cinntra.vistadelivery.model.BPModel.AddBranchResponse;
import com.cinntra.vistadelivery.model.BPModel.AddBusinessPartnerData;
import com.cinntra.vistadelivery.model.AddCampaignModel;
import com.cinntra.vistadelivery.model.AddOpportunity;
import com.cinntra.vistadelivery.model.AddQuotation;
import com.cinntra.vistadelivery.model.AdressDetail;
import com.cinntra.vistadelivery.model.BPModel.AddContactModel;
import com.cinntra.vistadelivery.model.BPModel.AtatchmentListModel;
import com.cinntra.vistadelivery.model.BPModel.BPAllFilterRequestModel;
import com.cinntra.vistadelivery.model.BPModel.BranchOneResponseModel;
import com.cinntra.vistadelivery.model.BPModel.BusinessPartnerAllResponse;
import com.cinntra.vistadelivery.model.BPModel.BussinessPartnerDetailModel;
import com.cinntra.vistadelivery.model.BPModel.ContactOneAPiModel;
import com.cinntra.vistadelivery.model.BPModel.UpdateBranchRequestModel;
import com.cinntra.vistadelivery.model.BPModel.UpdateContactDataModel;
import com.cinntra.vistadelivery.model.BPTypeResponse;
import com.cinntra.vistadelivery.model.Branch;
import com.cinntra.vistadelivery.model.BranchResponse;
import com.cinntra.vistadelivery.model.BPModel.BusinessPartnerData;
import com.cinntra.vistadelivery.model.CampaignListModel;
import com.cinntra.vistadelivery.model.CampaignListResponse;
import com.cinntra.vistadelivery.model.CampaignModel;
import com.cinntra.vistadelivery.model.CampaignResponse;
import com.cinntra.vistadelivery.model.ChatModel;
import com.cinntra.vistadelivery.model.ChatResponse;
import com.cinntra.vistadelivery.model.CompleteStageResponse;
import com.cinntra.vistadelivery.model.ContactExtension;
import com.cinntra.vistadelivery.model.ContactPerson;
import com.cinntra.vistadelivery.model.ContactPersonData;
import com.cinntra.vistadelivery.model.ContactPersonResponseModel;
import com.cinntra.vistadelivery.model.CounterResponse;
import com.cinntra.vistadelivery.model.CountryResponse;
import com.cinntra.vistadelivery.model.CreateCalenderActivityRequest;
import com.cinntra.vistadelivery.model.CreateContactData;
import com.cinntra.vistadelivery.model.CreateLead;
import com.cinntra.vistadelivery.model.CustomerBusinessRes;
import com.cinntra.vistadelivery.model.DeliveryResponse;
import com.cinntra.vistadelivery.model.DemoResponse;
import com.cinntra.vistadelivery.model.DemoValue;
import com.cinntra.vistadelivery.model.DepartMentDetail;
import com.cinntra.vistadelivery.model.EmpDetails;
import com.cinntra.vistadelivery.model.EmployeeProfile;
import com.cinntra.vistadelivery.model.EmployeeValue;
import com.cinntra.vistadelivery.model.EventResponse;
import com.cinntra.vistadelivery.model.EventValue;
import com.cinntra.vistadelivery.model.ExpenseNewModelResponse;
import com.cinntra.vistadelivery.model.ExpenseResponse;
import com.cinntra.vistadelivery.model.FollowUpData;
import com.cinntra.vistadelivery.model.FollowUpResponse;
import com.cinntra.vistadelivery.model.HeirarchiResponse;
import com.cinntra.vistadelivery.model.IndustryResponse;
import com.cinntra.vistadelivery.model.InventoryResponse;
import com.cinntra.vistadelivery.model.InvoiceResponse;
import com.cinntra.vistadelivery.model.ItemCategoryResponse;
import com.cinntra.vistadelivery.model.ItemResponse;
import com.cinntra.vistadelivery.model.LeadTypeResponse;
import com.cinntra.vistadelivery.model.LocationLatLongResponse;
import com.cinntra.vistadelivery.model.LogInDetail;
import com.cinntra.vistadelivery.model.LogInRequest;
import com.cinntra.vistadelivery.model.LogInResponse;
import com.cinntra.vistadelivery.model.MapData;
import com.cinntra.vistadelivery.model.MapResponse;
import com.cinntra.vistadelivery.model.NewEmployeeUser;
import com.cinntra.vistadelivery.model.NewLogINResponse;
import com.cinntra.vistadelivery.model.NewOppResponse;
import com.cinntra.vistadelivery.model.NewQuotation;
import com.cinntra.vistadelivery.model.NotificationData;
import com.cinntra.vistadelivery.model.NotificationResponse;
import com.cinntra.vistadelivery.model.OpportunityModels.OppActivityUpdateResponse;
import com.cinntra.vistadelivery.model.OpportunityModels.OppAddressResponseModel;
import com.cinntra.vistadelivery.model.OpportunityModels.OpportunitiesResponse;
import com.cinntra.vistadelivery.model.OpportunityModels.OpportunityAllListRequest;
import com.cinntra.vistadelivery.model.OpportunityModels.OpportunityItem;
import com.cinntra.vistadelivery.model.OpportunityModels.OpportunityStageResponse;
import com.cinntra.vistadelivery.model.OpportunityModels.UpdateActivityEventModel;
import com.cinntra.vistadelivery.model.OwnerResponse;
import com.cinntra.vistadelivery.model.PayMentTermsDetail;
import com.cinntra.vistadelivery.model.PaymentRespnse;
import com.cinntra.vistadelivery.model.PerformaInvoiceModel.PerformaInvoiceListRequestModel;
import com.cinntra.vistadelivery.model.PerformaInvoiceModel.QuotationOneAPiModel;
import com.cinntra.vistadelivery.model.PerformaInvoiceModel.QuotationUpdateModel;
import com.cinntra.vistadelivery.model.PerformaInvoiceModel.UpdateProformaInvoiceRequestModel;
import com.cinntra.vistadelivery.model.PostBP;
import com.cinntra.vistadelivery.model.QuotationItem;
import com.cinntra.vistadelivery.model.QuotationResponse;
import com.cinntra.vistadelivery.model.QuotationStringResponse;
import com.cinntra.vistadelivery.model.ResponseBusinessPartnerDropDown;
import com.cinntra.vistadelivery.model.ResponseBusinessType;
import com.cinntra.vistadelivery.model.ResponseCompanyBranchAllFilter;
import com.cinntra.vistadelivery.model.ResponseModel;
import com.cinntra.vistadelivery.model.ResponseZoneDropDown;
import com.cinntra.vistadelivery.model.RoleListDetail;
import com.cinntra.vistadelivery.model.SaleEmployeeResponse;
import com.cinntra.vistadelivery.model.SalesEmployeeItem;
import com.cinntra.vistadelivery.model.SalesTargetResponse;
import com.cinntra.vistadelivery.model.StagesResponse;
import com.cinntra.vistadelivery.model.StagesValue;
import com.cinntra.vistadelivery.model.StateData;
import com.cinntra.vistadelivery.model.StateDetail;
import com.cinntra.vistadelivery.model.StateRespose;
import com.cinntra.vistadelivery.model.TaxItemResponse;
import com.cinntra.vistadelivery.model.TokenResponseModel;
import com.cinntra.vistadelivery.model.Top5CustomerResponse;
import com.cinntra.vistadelivery.model.Top5ItemResponse;
import com.cinntra.vistadelivery.model.UpdateFavourites;
import com.cinntra.vistadelivery.model.UpdateLeadModel;
import com.cinntra.vistadelivery.model.UpdateQuotationModel;
import com.cinntra.vistadelivery.model.UserCounterResponse;
import com.cinntra.vistadelivery.model.UserIDResponse;
import com.cinntra.vistadelivery.model.UserLoginCredential;
import com.cinntra.vistadelivery.model.UserProfile;
import com.cinntra.vistadelivery.model.UserResponse;
import com.cinntra.vistadelivery.model.expenseModels.ExpenseOneDataResponseModel;
import com.cinntra.vistadelivery.model.orderModels.OrderDetailResponseModel;
import com.cinntra.vistadelivery.model.orderModels.OrderListModel;
import com.cinntra.vistadelivery.modelfilter.FilterOverAll;
import com.cinntra.vistadelivery.newapimodel.AddOpportunityModel;
import com.cinntra.vistadelivery.newapimodel.GlobalResponse;
import com.cinntra.vistadelivery.newapimodel.LeadDocumentResponse;
import com.cinntra.vistadelivery.newapimodel.LeadResponse;
import com.cinntra.vistadelivery.newapimodel.OpportunityValue;
import com.cinntra.vistadelivery.newapimodel.ResponseOrderListDropDown;
import com.cinntra.vistadelivery.newapimodel.ResponseQuoteListDropDown;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface ApiServices {
    @POST("b1sev1/Login")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<LogInResponse> LogIn(@Body LogInRequest data);







    @POST("login/")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<TokenResponseModel> loginToken(@Body JsonObject jsonObject);

    @POST("api/register/")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<String> Register(@Body String data);


    @GET("b1s/v1/Quotations?$select=DocNum,CardCode,DocEntry,DocDate&$orderby=DocEntry desc")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<QuotationResponse> QuotationList_Decending();

    @GET("b1s/v1/Quotations?$orderby=DocEntry desc")
    Call<QuotationResponse> quotationList();

    @GET
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<QuotationResponse> QuotationList(@Url String url);
    //@GET("b1s/v1/Quotations?$filter=DocumentStatus eq 'bost_Open'&$orderby=DocEntry desc &$top={top}")


    @POST("quotation/all_filter")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<QuotationResponse> OpenQuotationList(@Body HashMap<String, String> opportunityValue);

    @POST("quotation/all_filter_page")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<QuotationResponse> getQuotationList(@Body PerformaInvoiceListRequestModel performaInvoiceListRequestModel);


    @GET("businesspartner/alltype")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<ResponseBusinessType> getBusinessType();


    @POST("dropdown/zone/all_filter_page")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<ResponseZoneDropDown> getZoneDropDownApi(@Body PerformaInvoiceListRequestModel performaInvoiceListRequestModel);


    @POST("businesspartner/all_bp")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<ResponseBusinessPartnerDropDown> getBusinessPartnerListInDropDown(@Body JsonObject jsonObject);


    /*   @GET("b1s/v1/SalesOpportunities/")
       @Headers({ "Content-Type: application/json;charset=UTF-8"})
       Call<OpportunitiesResponse> OpportunitiesList();*/
    @GET
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<OpportunitiesResponse> OpportunitiesList(@Url String url);

    @GET("b1s/v1/SalesOpportunities?$filter=Status eq 'sos_Open'&$orderby=SequentialNo desc")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<OpportunitiesResponse> OpportunitiesOpenList();

    @GET
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<OpportunitiesResponse> OpportunitiesOpenList(@Url String url);

    @GET("b1s/v1/SalesOpportunities?$filter=Status eq 'sos_Sold'&$orderby=SequentialNo desc")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<OpportunitiesResponse> OpportunitiesWonList();

    @GET
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<OpportunitiesResponse> OpportunitiesWonList(@Url String url);

    @GET("b1s/v1/SalesOpportunities?$filter=Status eq 'sos_Missed'&$orderby=SequentialNo desc")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<OpportunitiesResponse> OpportunitiesLostList();

    @GET
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<OpportunitiesResponse> OpportunitiesLostList(@Url String url);

    //@GET("b1s/v1/Invoices?$filter=DocDueDate lt '2021-02-05'")
    @GET
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<InvoiceResponse> InvoicesOverDueList(@Url String url);

    @GET("b1s/v1/Invoices/")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<QuotationResponse> InvoicesList();

    @GET
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<InvoiceResponse> InvoicesList(@Url String url);

    @POST("order/all_filter_page")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<OrderListModel> OrdersList(@Body PerformaInvoiceListRequestModel opportunityValue);

    @POST("order/one")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<OrderDetailResponseModel> getOrderOneDetail(@Body JsonObject jsonObject);


    @POST("order/ord_attachment_create")
    Call<AttachmentResponseModel> postAttachmentUploadApiOrder(@Body MultipartBody requestBody);


    @GET
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<QuotationResponse> OrdersList(@Url String url);

    @GET("b1s/v1/Orders?$filter=DocumentStatus eq 'bost_Open'&$orderby=DocEntry desc")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<QuotationResponse> OrdersOpenList();

    @GET
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<QuotationResponse> OrdersOpenList(@Url String url);

    @GET("b1s/v1/DeliveryNotes?$filter=DocumentStatus eq 'bost_Open'")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<QuotationResponse> deliveryOpenList();

    @GET("b1s/v1/DeliveryNotes?$filter=DocumentStatus eq 'C' and Cancelled eq 'tNO'")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<QuotationResponse> deliveryCloseList();

    // @GET("b1s/v1/DeliveryNotes?$filter=DocDueDate lt '2021-02-05'")
    @GET
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<QuotationResponse> deliveryOverDueList(@Url String url);

    @GET("b1s/v1/DeliveryNotes/")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<QuotationResponse> deliveryList();

    @GET("b1s/v1/InventoryGenEntries/")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<InvoiceResponse> inventoryList();


    @POST("item/all")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<ItemResponse> ItemsList(@Body JsonObject url);

    @GET("b1s/v1/SalesTaxCodes?$select=Code")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<TaxItemResponse> taxcodes();

    /*******************  Add New APIs  *********************/
    @POST("b1s/v1/Quotations")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<QuotationResponse> NewQuotation(@Body NewQuotation in);

    @POST("order/create")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<QuotationResponse> addOrder(@Body AddQuotation in);

    @POST("quotation/create")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<QuotationResponse> addQuotation(@Body AddQuotation in);

    @POST("b1s/v1/SalesOpportunities")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<QuotationResponse> addOpportunity(@Body AddOpportunity in);

    /*******************  Update APIs  *********************/

    //@PUT("b1s/v1/Quotations({id})")
    @POST("quotation/update")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<QuotationUpdateModel> updateQuotation(@Body UpdateProformaInvoiceRequestModel model);


    @PATCH("b1s/v1/SalesOpportunities({id})")
    Call<QuotationResponse> updateOpportunity(@Path("id") String id, @Body AddOpportunity model);

    @PATCH("b1s/v1/SalesOpportunities({id})")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<QuotationResponse> updateFavorite(@Path("id") String id, @Body UpdateFavourites model);

    @POST("opportunity/fav")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<NewOppResponse> updateoppFavorite(@Body UpdateFavourites model);

    @POST("order/update")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<QuotationResponse> updateOrder(@Body UpdateQuotationModel model);

    @PATCH("b1s/v1/BusinessPartners('{id}')")
    Call<QuotationResponse> AddContact(@Path("id") String id, @Body ContactExtension model);

    @POST("b1s/v1/BusinessPartners")
    Call<QuotationResponse> AddBP(@Body PostBP model);

    @POST("opportunity/update")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<NewOppResponse> newUpdateOpportunity(@Body AddOpportunityModel model);

    @PATCH("b1s/v1/BusinessPartners('{id}')")
    Call<QuotationResponse> AddBP(@Path("id") String id, @Body PostBP model);



  /*  @GET("b1s/v1/SalesPersons/")
    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    Call<SaleEmployeeResponse> getSalesEmplyeeList();*/


    @GET("employee/all")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<SaleEmployeeResponse> getSalesEmplyeeList();

    @POST("employee/all_filter")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<SaleEmployeeResponse> getSalesEmplyeeList(@Body EmployeeValue employeeValue);

    @POST("activity/maps")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<LocationLatLongResponse> getCurrentLocationLatLong(@Body JsonObject jsonObject);

    @POST("businesspartner/employee/all")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<ContactPerson> ContactEmployeesList(@Body ContactPersonData businessPartnerData);


    @GET("b1s/v1/Countries/")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<AdressDetail> getCountryName();

    @GET
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<AdressDetail> getCountryName(@Url String url);

    @GET("b1s/v1/States/")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<StateDetail> getStateName();

    @GET
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<StateDetail> getStateName(@Url String url);

    /*@GET("b1s/v1/Departments/")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<DepartMentDetail> getDepartMent();*/

    @GET("businesspartner/department/all")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<DepartMentDetail> getDepartMent();

    /*@GET("b1s/v1/EmployeePosition/")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<RoleListDetail> getRole();*/

    @GET("businesspartner/position/all")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<RoleListDetail> getRole();

    @GET("XSJS/SalesDashBoard.xsjs?User=E02558&DBName=JCSPL&Position=Level1")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<String> getNewData();


    /******************** Counter Apis****************************/

    @GET("b1s/v1/EmployeesInfo?$select=EmployeeID,FirstName,MiddleName,LastName")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<OwnerResponse> Employees_Owener_List();

    @GET("b1s/v1/Quotations?$apply=aggregate($count as Count)")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<CounterResponse> QuotationCount();

    @GET("b1s/v1/SalesOpportunities?$apply=aggregate($count as Count)")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<CounterResponse> OpportunityCount();

    @GET("b1s/v1/Orders?$apply=aggregate($count as Count)")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<CounterResponse> OrdersCount();


    @POST("employee/invoice_counter")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<CounterResponse> InvoicesCount(@Body SalesEmployeeItem salesEmployeeItem);

    @GET("b1s/v1/BusinessPartners?$apply=aggregate($count as Count)")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<CounterResponse> CustomerCount();

    /************************** one Time/Same Apis  *****************************/

    @GET("b1s/v1/SalesStages/")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<StagesResponse> getStagesList();

    @GET("industries/all")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<IndustryResponse> getIndustryList();

    /************************** Profile  Apis  *****************************/
    @GET
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<UserIDResponse> getUserID(@Url String url);

    @GET
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<UserResponse> getUserProfile(@Url String url);

    @GET
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<UserProfile> getUserProfileDetail(@Url String url);


    /******************************* Hana Apis **********************************/

    @GET("SalesApp_Cinntra_Test/Opportunity/GetOpportunity.xsjs?DBName=TEST")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<String> TestApi();

 /*   @GET
    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    Call<InventoryResponse> getAllInventories(@Url String url);
*/

    @GET
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<DeliveryResponse> getOpenDeliveries(@Url String url);

    @GET
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<DeliveryResponse> getCloseDeliveries(@Url String url);

    @GET
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<DeliveryResponse> getOverDueDeliveries(@Url String url);

    @GET
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<SalesTargetResponse> getSalesTarget(@Url String url);

    @POST("employee/top5itembyamount")
    Call<Top5ItemResponse> getTop5Items(@Body HashMap<String, String> hs);

    @POST("employee/top5bp")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<Top5CustomerResponse> getTop5Customer(@Body HashMap<String, String> hs);
  /*  @GET
    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    Call<HeirarchiResponse> getHeirarchi(@Url String url);*/


    @POST("employee/all_filter")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<HeirarchiResponse> getAllEmployeelist(@Body EmployeeValue employeeValue);

    @POST("businesspartner/branch/all")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<BranchResponse> getBranch(@Body Branch branch);

    @GET
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<UserCounterResponse> getUserCounter(@Url String url);

    @GET
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<UserLoginCredential> getLogIn(@Url String url);

    /*    @GET("b1s/v1/PaymentTermsTypes/")
        @Headers({"Content-Type: application/json;charset=UTF-8"})
        Call<PayMentTermsDetail> getPaymentTerm();*/
    @GET("paymenttermstypes/all")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<PayMentTermsDetail> getPaymentTerm();


    @POST("quotation/fav")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<QuotationResponse> updateFavQuotation(@Body QuotationItem model);


    @POST("stage/all")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<OpportunityStageResponse> getAllStages(@Body OpportunityItem model);

    @POST("stage/create")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<OpportunityStageResponse> createStages(@Body StagesValue model);


    @POST("demo/create")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<DemoResponse> createDemo(@Body DemoValue model);

    /*@GET("opportunity/all")
    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    Call<NewOppResponse> allopportinitylist();*/

    @POST("opportunity/all_filter")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<NewOppResponse> allopportinitylist(@Body OpportunityValue opportunityValue);


    @POST("opportunity/one")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<NewOppResponse> getparticularopportunity(@Body OpportunityValue opportunityValue);

    @POST("activity/chatter_all")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<ChatResponse> getAllChat(@Body StagesValue opportunityValue);

    @POST("activity/chatter")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<ChatResponse> createChat(@Body ChatModel opportunityValue);

    @POST("opportunity/create")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<NewOppResponse> createopportunity(@Body AddOpportunityModel opportunityValue);


    @POST("lead/all_filter_page")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<LeadResponse> getAllLead(@Body FilterOverAll leadValue);

    @POST("activity/create")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<EventResponse> createnewevent(@Body EventValue eventValue);


    @POST("activity/create")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<EventResponse> createnewevent(@Body CreateCalenderActivityRequest eventValue);


    @POST("activity/all_filter_by_date")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<EventResponse> getCalendarData(@Body JsonObject eventValue);


    @POST("activity/all_filter")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<EventResponse> getcalendardata(@Body SalesEmployeeItem eventValue);

    @POST("activity/update")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<OppActivityUpdateResponse> updateOppEventActivity(@Body UpdateActivityEventModel eventValue);


    @POST("activity/followup")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<FollowUpResponse> createfollowUP(@Body FollowUpData eventValue);


    @POST("activity/all")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<EventResponse> getallevent(@Body EventValue eventValue);


    @POST("activity/delete")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<EventResponse> deleteEvent(@Body EventValue eventValue);


    @POST("activity/one")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<EventResponse> particularevent(@Body EventValue eventValue);


    @POST("activity/update")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<EventResponse> updateevent(@Body EventValue eventValue);


    @POST("opportunity/change_stage")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<OpportunityStageResponse> updatestage(@Body StagesValue stval);


    @POST("opportunity/complete")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<OpportunityStageResponse> completestage(@Body CompleteStageResponse stval);


    @POST("employee/login")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<NewLogINResponse> loginEmployee(@Body LogInDetail logInDetail);

    @GET("employee/get_user_info")
    Call<NewLogINResponse> loginEmployeeGetUserInfo();


    @POST("login/")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<NewLogINResponse> sessionlogin(@Body HashMap<String, String> logInDetail);


    @POST("stage/stage_detail")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<OpportunityStageResponse> getStagesComment(@Body StagesValue oppitem);


    @POST("lead/one")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<LeadResponse> particularlead(@Body JsonObject leadValue);

    @POST("lead/lead_attachments")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<LeadDocumentResponse> particularleadattachment(@Body HashMap<String, Integer> leadValue);


    @POST("lead/lead_attachment_delete")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<LeadDocumentResponse> deleteLeadAttachment(@Body JsonObject leadValue);


    @POST("quotation/quot_attachment_delete")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<LeadDocumentResponse> deleteQuotAttachment(@Body JsonObject leadValue);


    @POST("order/ord_attachment_delete")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<LeadDocumentResponse> deleteOrderAttachment(@Body JsonObject leadValue);


    @POST("payment/payment_img_delete")
    Call<LeadDocumentResponse> deletePaymentAttachment(@Body JsonObject requestBody);


    @GET("businesspartner/all")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<CustomerBusinessRes> getAllBusinessPartner();

    @GET("businesspartner/all")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<BusinessPartnerAllResponse> getAllParentAccBP();


    //todo bp list..
    @POST("businesspartner/all_filter_page")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<BusinessPartnerAllResponse> getBPAllPageList(@Body BPAllFilterRequestModel requestModel); //BusinessPartnerAllListResponseModel


    // todo bp list..
    @POST("company/branch/all_filter")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<ResponseCompanyBranchAllFilter> getBranchAllFilter(@Body BPAllFilterRequestModel requestModel); //BusinessPartnerAllListResponseModel


    @POST("businesspartner/create")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<CustomerBusinessRes> addnewCustomer(@Body AddBusinessPartnerData in);

    @POST("businesspartner/update")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<CustomerBusinessRes> updatecustomer(@Body AddBusinessPartnerData businessPartnerData);

    @POST("businesspartner/one")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<CustomerBusinessRes> particularcustomerdetails(@Body BusinessPartnerData businessPartnerData);

    @POST("businesspartner/one")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<BussinessPartnerDetailModel> businessPartnerDetailOne(@Body JsonObject jsonObject);

    @POST("businesspartner/one")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<BusinessPartnerAllResponse> callBPOneAPi(@Body JsonObject jsonObject);



    @POST("quotation/all_quot")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<ResponseQuoteListDropDown> callQuoteListDropDown(@Body JsonObject jsonObject);

    @POST("order/all_ord")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<ResponseOrderListDropDown> callOrderListDropDown(@Body JsonObject jsonObject);


    @POST("businesspartner/bp_attachments")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<AtatchmentListModel> getAllBPAttchmentList(@Body JsonObject jsonObject);


    @POST("businesspartner/branch/all")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<OppAddressResponseModel> getShipToAddress(@Body JsonObject jsonObject);


    @POST("quotation/approve")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<ResponseModel> quotationApprove(@Body JsonObject jsonObject);

    @POST("businesspartner/bp_attachment_create")
    Call<AtatchmentListModel> createBPAttachment(@Body MultipartBody jsonObject);


    @POST("businesspartner/employee/delete")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<ContactOneAPiModel> deleteContact(@Body JsonObject contactData);

    @POST("businesspartner/branch/delete")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<BranchResponse> deleteBranch(@Body JsonObject branch);


    //todo add Branches ...
    @POST("businesspartner/branch/create")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<AddBranchResponse> addBranchApi(@Body AddBranchRequestModel body);


    @POST("businesspartner/branch/update")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<AddBranchResponse> updateBranchApi(@Body UpdateBranchRequestModel body);

    @POST("businesspartner/branch/one")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<BranchOneResponseModel> getBranchOneApi(@Body JsonObject jsonObject);


    @POST("businesspartner/employee/one")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<ContactOneAPiModel> getContactOneApi(@Body JsonObject businessPartnerData);


    @POST("businesspartner/employee/update")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<ContactOneAPiModel> updateContact(@Body UpdateContactDataModel contactData);


    @POST("businesspartner/bp_attachment_delete")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<AtatchmentListModel> deleteBPAttachment(@Body JsonObject jsonObject);


    @POST("businesspartner/employee/all")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<ContactPerson> contactemplist(@Body ContactPersonData contactPersonData);


    @POST("opportunity/all_filter_page")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<NewOppResponse> getOpportunityList(@Body OpportunityAllListRequest opportunityAllListRequest);


    @POST("businesspartner/employee/create")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<AddContactModel> createcontact(@Body CreateContactData contactData);


    @POST("businesspartner/branch/create")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<BranchResponse> addBranch(@Body Branch branch);


    @GET("countries/all")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<CountryResponse> getCountryList();


    @POST("states/all")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<StateRespose> getStateList(@Body StateData stateData);


    @POST("businesspartner/branch/update")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<BranchResponse> updateBranch(@Body Branch branch);


    @POST("employee/dashboard")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<CounterResponse> dashboardcounter(@Body SalesEmployeeItem salesEmployeeItem);

    @POST("employee/analytics")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<CounterResponse> projectiondata(@Body SalesEmployeeItem salesEmployeeItem);


    @POST("activity/maps")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<MapResponse> sendMaplatlong(@Body MapData mapData);

    @POST("notification/all")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<NotificationResponse> allnotification(@Body SalesEmployeeItem salesEmployeeItem);


    @POST("order/delivery")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<QuotationStringResponse> orderlist(@Body SalesEmployeeItem salesEmployeeItem);


    @POST("activity/status")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<EventResponse> completeEvent(@Body EventValue eventValue);

    @POST("lead/create")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<LeadResponse> createLead(@Body ArrayList<CreateLead> createLeads);
/*
    @POST("lead/chatter_all")
    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    Call<ChatResponse> getAllLeadChat(@Body LeadChatModel opportunityValue);*/


    @POST("activity/chatter_all")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<ChatResponse> getAllLeadChat(@Body FollowUpData opportunityValue);


    @POST("lead/chatter")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<ChatResponse> createleadChat(@Body ChatModel chatModel);


    @GET("invoice/all")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<InvoiceResponse> getallinvoice();


    @POST("invoice/all_filter_page")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<InvoiceResponse> getAllInvoiceFilter(@Body PerformaInvoiceListRequestModel performaInvoiceListRequestModel);


    @POST("employee/one")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<EmployeeProfile> getProfileDetail(@Body EmpDetails empDetails);


    @GET("lead/type_all")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<LeadTypeResponse> getLeadType();


    @POST("businesspartner/one")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<ContactPersonResponseModel> getContactPerson(@Body JsonObject jsonObject);

    @GET("lead/source_all")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<LeadTypeResponse> getsourceType();

    @POST("notification/read")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<NotificationResponse> readnotification(@Body NotificationData nd);


    @POST("lead/update")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<GlobalResponse> updateLead(@Body UpdateLeadModel lv);


    @GET("item/category_all")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<ItemCategoryResponse> getAllCategory();


    @POST("category/all_filter")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<ItemCategoryResponse> getAllCategory(@Body EmployeeValue opportunityValue);


    @POST("employee/create")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<LeadResponse> createdemoEmployee(@Body NewEmployeeUser newEmployeeUser);


    @GET("campset/all")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<CampaignResponse> getAllCampaign();


    @POST("campset/create")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<CampaignResponse> createCampaign(@Body AddCampaignModel campaignModel);


    @POST("campset/one")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<CampaignResponse> getCampsetDetails(@Body CampaignModel cm);

    @POST("camp/filter_campaign")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<CampaignListModel> getmemberlist(@Body CampaignListResponse cm);

    @GET("businesspartner/alltype")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<BPTypeResponse> getBptypelist();

    @GET("opportunity/alltype")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<BPTypeResponse> getopptypelist();

    @POST("quotation/one")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<QuotationOneAPiModel> oneQuotationApi1(@Body JsonObject jsonObject);


    @POST("payment/one")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<PaymentRespnse> onePaymentApi(@Body JsonObject jsonObject);


    @POST("quotation/quot_attachment_create")
    Call<AttachmentResponseModel> postAttachmentUploadApi(@Body MultipartBody requestBody);

    @POST("attachment/create")
    Call<AttachmentResponseModel> attachmentCreated(@Body MultipartBody requestBody);


    @POST("lead/lead_attachment_create")
    Call<LeadResponse> updateLeadattachment(@Body MultipartBody requestBody);


    @POST("employee/movingitems")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<InventoryResponse> getInventorylist(@Body HashMap<String, String> st);


    @GET("expense/all")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<ExpenseNewModelResponse> getAllExpense();

    @GET("payment/all")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<PaymentRespnse> getAllpaymentDetails();

    @POST("expense/create")
    Call<ExpenseResponse> createexpense(@Body MultipartBody requestBody);


    @POST("payment/create")
    Call<PaymentRespnse> createpaymentdetails(@Body MultipartBody requestBody);

    @POST("expense/update")
    Call<com.cinntra.vistadelivery.model.expenseModels.ExpenseResponse> updateexpense(@Body MultipartBody requestBody);

    @POST("payment/update")
    Call<ExpenseResponse> updatepayment(@Body MultipartBody requestBody);

    @POST("expense/delete")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<ExpenseResponse> deleteexpense(@Body HashMap<String, List<String>> hd);

    @POST("expense/one")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<ExpenseOneDataResponseModel> getExpenseOneData(@Body JsonObject jsonObject);


    @POST("payment/delete")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<PaymentRespnse> deletepayment(@Body HashMap<String, List<Integer>> hd);

    @POST("activity/map_filter")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<MapResponse> getmaplocation(@Body HashMap<String, String> mapData);
}

