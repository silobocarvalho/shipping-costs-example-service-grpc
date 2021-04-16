package br.com.zup.ot2

import io.grpc.ServerBuilder
import io.grpc.Status
import io.grpc.stub.StreamObserver
import java.lang.IllegalStateException
import kotlin.random.Random


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
    //assume that request is not null with !! operator
        val zipcode = request!!.zipcode

        //Zipcode validation block
        zipcode.let {
            if(it == null || it.isBlank()){
                val error = Status.INVALID_ARGUMENT
                    .withDescription("Zipcode must be filled")
                    .asRuntimeException()
                responseObserver?.onError(error)
            }

            if(!it.matches("[0-9]{5}-[0-9]{3}".toRegex())){
                responseObserver?.onError((
                        Status.INVALID_ARGUMENT
                            .withDescription("Invalid zipcode")
                            .augmentDescription("Should follow: 11111-000 format")
                            .asRuntimeException()
                        ))
                return
            }
        }

        //Some errors should not be returned to client, for that, we use internal error handler.

        var value = 0.0
        try {
            value = Random.nextDouble(from = 0.0, until = 140.0)
            if(value > 100){
                throw IllegalStateException("Simulated error with confidential data!")
            }
        }catch(e: Exception){
            println(e)
            responseObserver?.onError(Status.INTERNAL
                .withDescription(e.message) // sent to client
                .withCause(e) //not sent to client : type of exception: java.lang.IllegalStateException
                .asRuntimeException() )

            //ErrorDetails can be thorow here using Proto file.
        }


        val response = CalculateShippingCostsResponse.newBuilder()
            .setZipcode(request?.zipcode)
            .setPrice(150.22)
            .build()

        responseObserver?.onNext(response)
        responseObserver?.onCompleted()
    }
}