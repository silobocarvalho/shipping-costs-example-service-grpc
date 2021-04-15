package br.com.zup.ot2

import io.grpc.ServerBuilder
import io.grpc.stub.StreamObserver


fun main(){

    val server = ServerBuilder
        .forPort(50051)
        .addService(ShippingCalculatorGrpcServer())
        .build()

    server.start()

    server.awaitTermination()

}

class ShippingCalculatorGrpcServer : ShippingCostsServiceGrpc.ShippingCostsServiceImplBase() {

    override fun calculateShippingCosts(
        request: CalculateShippingCostsRequest?,
        responseObserver: StreamObserver<CalculateShippingCostsResponse>?
    ) {
        val response = CalculateShippingCostsResponse.newBuilder()
            .setZipcode(request?.zipcode)
            .setPrice(150.22)
            .build()

        responseObserver?.onNext(response)
        responseObserver?.onCompleted()
    }
}