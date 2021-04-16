package br.com.zup.ot2

import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.exceptions.HttpStatusException
import javax.inject.Inject

@Controller
class ShippingCostsController(@Inject val grpcClient: ShippingCostsServiceGrpc.ShippingCostsServiceBlockingStub) {

    @Get("/api/shipping")
    fun calculate(@QueryValue zipcode: String) : ShippingCostsResponse {
        println("entrou no endpoint")
        val request = CalculateShippingCostsRequest.newBuilder()
            .setZipcode(zipcode)
            .build()

        try {
            val response = grpcClient.calculateShippingCosts(request)
            return ShippingCostsResponse(zipcode = response.zipcode, price = response.price)
        } catch (e: StatusRuntimeException) {
            //Error from server RPC
            if (e.status.code == Status.Code.INVALID_ARGUMENT) {
                throw HttpStatusException(HttpStatus.BAD_REQUEST, e.status.description)
            }

            //Example, not really used.
            if (e.status.code == Status.Code.PERMISSION_DENIED) {
                //if null, throw exception
                val statusProto = StatusProto.fromThrowable(e)
                    ?: throw HttpStatusException(HttpStatus.FORBIDDEN, e.status.description)
                val moreDetails = statusProto.detailsList.get(0).unpack(ErrorDetails::class.java)
                throw HttpStatusException(HttpStatus.FORBIDDEN, "${moreDetails.code}: ${moreDetails.message}")
            }
            throw HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }
    }
}

data class ShippingCostsResponse(val zipcode: String, val price: Double){}