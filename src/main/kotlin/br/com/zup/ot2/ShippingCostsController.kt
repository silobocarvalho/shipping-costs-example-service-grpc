package br.com.zup.ot2

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import javax.inject.Inject

@Controller
class ShippingCostsController(@Inject val grpcClient: ShippingCostsServiceGrpc.ShippingCostsServiceBlockingStub) {

    @Get("/api/shipping")
    fun calculate(@QueryValue zipcode: String) : ShippingCostsResponse{
        println("entrou no endpoint")
        val request = CalculateShippingCostsRequest.newBuilder()
            .setZipcode(zipcode)
            .build()

        val response = grpcClient.calculateShippingCosts(request)

        return ShippingCostsResponse(zipcode = response.zipcode, price = response.price)

    }
}

data class ShippingCostsResponse(val zipcode: String, val price: Double){}