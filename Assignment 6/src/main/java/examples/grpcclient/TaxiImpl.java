package example.grpcclient;

import io.grpc.stub.StreamObserver;
import service.FareRequest;
import service.FareResponse;
import service.TaxiCompanyServiceGrpc;

public class TaxiImpl extends TaxiCompanyServiceGrpc.TaxiCompanyServiceImplBase {

    @Override
    public void calculateFare(FareRequest request, StreamObserver<FareResponse> responseObserver) {
        double totalDistance = request.getTotalDistance();
        int totalTime = request.getTotalTime();
        double fare = calculateFare(totalDistance, totalTime);
        FareResponse response = FareResponse.newBuilder()
                .setTotalFare(fare)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private double calculateFare(double distance, int time) {
        double farePerMile = 2.5;
        double farePerMinute = 0.5;
        double totalFare = distance * farePerMile + time * farePerMinute;
        return totalFare;
    }
}
