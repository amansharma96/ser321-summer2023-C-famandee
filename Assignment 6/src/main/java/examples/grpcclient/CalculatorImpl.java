package example.grpcclient;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import service.*;

import java.util.ArrayList;
import java.util.List;

public class CalculatorImpl extends RecipeGrpc.RecipeImplBase {

    private final List<Expression> history;

    public CalculatorImpl() {
        this.history = new ArrayList<>();
    }

    @Override
    public void evaluate(EvaluateRequest request, StreamObserver<EvaluateResponse> responseObserver) {
        Expression expression = request.getExpression();
        int num1 = expression.getNum1();
        int num2 = expression.getNum2();
        Expression.Operation operation = expression.getOperation();

        System.out.println("Received from client: " + num1 + " " + operation + " " + num2);

        int result;
        String error = "";

        switch (operation) {
            case PLUS:
                result = num1 + num2;
                break;
            case MINUS:
                result = num1 - num2;
                break;
            case MULTIPLY:
                result = num1 * num2;
                break;
            case DIVIDE:
                if (num2 != 0) {
                    result = num1 / num2;
                } else {
                    result = 0;
                    error = "Division by zero is not allowed.";
                }
                break;
            default:
                result = 0;
                error = "Invalid operation.";
                break;
        }

        EvaluateResponse response = EvaluateResponse.newBuilder()
                .setIsSuccess(error.isEmpty())
                .setResult(result)
                .setError(error)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


    @Override
    public void history(Empty request, StreamObserver<HistoryResponse> responseObserver) {
        HistoryResponse response = HistoryResponse.newBuilder()
                .addAllExpressions(history)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
