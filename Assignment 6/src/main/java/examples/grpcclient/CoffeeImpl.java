package example.grpcclient;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import service.CoffeePotGrpc;
import service.BrewResponse;
import service.GetCupResponse;
import service.BrewStatusResponse;
import service.BrewStatus;

public class CoffeeImpl extends CoffeePotGrpc.CoffeePotImplBase {
    private int cups;

    public CoffeeImpl(int cups) {
        this.cups = cups;
    }

    @Override
    public void brew(Empty request, StreamObserver<BrewResponse> responseObserver) {
        if (cups > 0) {
            System.out.println("Coffee brewing has started");
            cups--;
            responseObserver.onNext(BrewResponse.newBuilder().setIsSuccess(true).build());
        } else {
            String error = "Out of coffee. Please refill.";
            System.out.println("Error: " + error);
            responseObserver.onNext(BrewResponse.newBuilder().setIsSuccess(false).setError(error).build());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void getCup(Empty request, StreamObserver<GetCupResponse> responseObserver) {
        if (cups > 0) {
            System.out.println("Enjoy your cup of coffee");
            cups--;
            responseObserver.onNext(GetCupResponse.newBuilder().setIsSuccess(true).build());
        } else {
            String error = "Out of coffee. Please refill.";
            System.out.println("Error: " + error);
            responseObserver.onNext(GetCupResponse.newBuilder().setIsSuccess(false).setError(error).build());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void brewStatus(Empty request, StreamObserver<BrewStatusResponse> responseObserver) {
        int minutes = 0;
        int seconds = 0;
        String message = "Brewing in progress";
        responseObserver.onNext(BrewStatusResponse.newBuilder()
                .setStatus(BrewStatus.newBuilder()
                        .setMinutes(minutes)
                        .setSeconds(seconds)
                        .setMessage(message)
                        .build())
                .build());
        responseObserver.onCompleted();
    }
}
