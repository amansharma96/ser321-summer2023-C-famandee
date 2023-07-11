package example.grpcclient;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import service.*;

import java.util.HashMap;
import java.util.Map;

public class ZooImpl extends ZooGrpc.ZooImplBase {
    private final Map<String, Animal> zooAnimals;
    public ZooImpl() {
        this.zooAnimals = new HashMap<>();
    }

    @Override
    public void add(AddAnimalRequest request, StreamObserver<AddAnimalResponse> responseObserver) {
        Animal animal = request.getAnimal();
        if (zooAnimals.containsKey(animal.getName())) {
            AddAnimalResponse response = AddAnimalResponse.newBuilder()
                    .setIsSuccess(false)
                    .setError("Animal with the same name already exists")
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        }
        zooAnimals.put(animal.getName(), animal);
        AddAnimalResponse response = AddAnimalResponse.newBuilder()
                .setIsSuccess(true)
                .setMessage("Animal added successfully")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void speak(SpeakRequest request, StreamObserver<SpeakResponse> responseObserver) {
        String animalName = request.getName();
        SpeakResponse.Builder responseBuilder = SpeakResponse.newBuilder();
        if (zooAnimals.containsKey(animalName)) {
            Animal animal = zooAnimals.get(animalName);
            responseBuilder.addAnimals(animal.getAnimalSound());
            responseBuilder.setIsSuccess(true);
        } else {
            responseBuilder.setError("Animal not found");
            responseBuilder.setIsSuccess(false);
        }
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void speakAll(Empty request, StreamObserver<SpeakResponse> responseObserver) {
        SpeakResponse.Builder responseBuilder = SpeakResponse.newBuilder();
        if (!zooAnimals.isEmpty()) {
            for (Animal animal : zooAnimals.values()) {
                responseBuilder.addAnimals(animal.getAnimalSound());
            }
            responseBuilder.setIsSuccess(true);
        } else {
            responseBuilder.setError("No animals in the zoo");
            responseBuilder.setIsSuccess(false);
        }
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }
}
