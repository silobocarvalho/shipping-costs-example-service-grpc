package br.com.zup.ot2

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import javax.inject.Singleton

@Factory
class GrpcClientFactory {
    //this value 'shipping' is get from application.yml
    @Singleton
    fun shippingCostsClientStub(@GrpcChannel("shipping") channel : ManagedChannel): ShippingCostsServiceGrpc.ShippingCostsServiceBlockingStub? {

        // Annotation above substitute this code
        /*
        val channel = ManagedChannelBuilder
            .forAddress("localhost", 50051)
            .usePlaintext()
            .maxRetryAttempts(10)
            .build()
*/
        return ShippingCostsServiceGrpc.newBlockingStub(channel)
    }
}

