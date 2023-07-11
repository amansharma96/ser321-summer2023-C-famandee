package example.grpcclient;

import io.grpc.stub.StreamObserver;
import service.GetBillRequest;
import service.GetBillResponse;
import service.RestaurantServiceGrpc;

public class RestaurantImpl extends RestaurantServiceGrpc.RestaurantServiceImplBase {

    @Override
    public void getBill(GetBillRequest request, StreamObserver<GetBillResponse> responseObserver) {
        String tableNumber = request.getTableNumber();
        int numPlates = request.getNumPlates();
        double plateCost = request.getPlateCost();
        double tipAmount = request.getTipAmount();
        double totalCost = numPlates * plateCost;
        double totalAmount = totalCost + tipAmount;
        GetBillResponse response = GetBillResponse.newBuilder()
                .setIsSuccess(true)
                .setMessage("Bill details for Table " + tableNumber)
                .setTotalCost(totalCost)
                .setTipAmount(tipAmount)
                .setTotalAmount(totalAmount)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
