package Backend;

import co.nstant.in.cbor.CborBuilder;
import co.nstant.in.cbor.CborEncoder;
import co.nstant.in.cbor.CborException;
import com.google.protobuf.ByteString;
import sawtooth.sdk.protobuf.*;
import sawtooth.sdk.signing.PrivateKey;
import sawtooth.sdk.signing.Secp256k1Context;
import sawtooth.sdk.signing.Signer;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import sawtooth.sdk.processor.Utils;
import sawtooth.sdk.signing.PrivateKey;
import sawtooth.sdk.signing.Secp256k1Context;
import sawtooth.sdk.signing.Signer;
import co.nstant.in.cbor.*;
import sawtooth.sdk.protobuf.TransactionHeader;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.List;
import java.util.UUID;
import com.google.protobuf.ByteString;
import sawtooth.sdk.protobuf.Transaction;
import com.google.protobuf.ByteString;
import sawtooth.sdk.protobuf.TransactionList;
import sawtooth.sdk.protobuf.BatchHeader;
import sawtooth.sdk.protobuf.Transaction;
import sawtooth.sdk.protobuf.Batch;
import sawtooth.sdk.protobuf.BatchList;



public class Sawtooth_main {
    public static void main(String[] args){
        Secp256k1Context context  = new Secp256k1Context();
        PrivateKey privateKey = context.newRandomPrivateKey();
        Signer signer = new Signer(context, privateKey);
        ByteArrayOutputStream payload = new ByteArrayOutputStream();
        try {
            new CborEncoder(payload).encode(new CborBuilder()
                    .addMap()
                    .put("Verb", "set")
                    .put("Name", "foo")
                    .put("Value", 42)
                    .end()
                    .build());
        } catch (CborException e) {
            e.printStackTrace();
        }
        byte[] payloadBytes = payload.toByteArray();

        TransactionHeader header = TransactionHeader.newBuilder()
                .setSignerPublicKey(signer.getPublicKey().hex())
                .setFamilyName("xo")
                .setFamilyVersion("1.0")
                .addInputs("1cf1266e282c41be5e4254d8820772c5518a2c5a8c0c7f7eda19594a7eb539453e1ed7")
                .addOutputs("1cf1266e282c41be5e4254d8820772c5518a2c5a8c0c7f7eda19594a7eb539453e1ed7")
                //.setPayloadSha512(Utils.hash512(payload))
                .setBatcherPublicKey(signer.getPublicKey().hex())
                .setNonce(UUID.randomUUID().toString())
                .build();

        String signature = signer.sign(header.toByteArray());

        Transaction transaction =  Transaction.newBuilder()
                .setHeader(header.toByteString())
                .setPayload(ByteString.copyFrom(payloadBytes))
                .setHeaderSignature(signature)
                .build();

        List<Transaction> transactions = new ArrayList();
        transactions.add(transaction);

        BatchHeader batchHeader = BatchHeader.newBuilder()
                .setSignerPublicKey(signer.getPublicKey().hex())
                .addAllTransactionIds(
                        transactions
                                .stream()
                                .map(Transaction::getHeaderSignature)
                                .collect(Collectors.toList())
                )
                .build();

        String batchSignature = signer.sign(batchHeader.toByteArray());

        Batch batch = Batch.newBuilder()
                .setHeader(batchHeader.toByteString())
                .addAllTransactions(transactions)
                .setHeaderSignature(batchSignature)
                .build();

        byte[] batchListBytes = BatchList.newBuilder()
                .addBatches(batch)
                .build()
                .toByteArray();
    }





    /*import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.converter.gson.GsonConverterFactory

interface SawtoothRestApi {
    @POST("/batches")
    fun postBatchList(@Body payload: RequestBody): Call<BatchListResponse>
}

val retrofit = Retrofit.Builder()
     .baseUrl("http://rest.api.domain/batches")
     .addConverterFactory(GsonConverterFactory.create())
     .build()

 val service = retrofit.create<SawtoothRestApi>(SawtoothRestApi::class.java)

 val body = RequestBody.create(
              MediaType.parse("application/octet-stream"),
              batchListBytes)

 val call1 = service.postBatchList(body)
 call1.enqueue(object : Callback<BatchListResponse> {
     override fun onResponse(call: Call<BatchListResponse>, response: Response<BatchListResponse>) {
           if (response.body() != null) {
               Log.d("Response", response.body().toString())
           } else {
               Log.d("Response", response.toString())
           }
     }
     override fun onFailure(call: Call<BatchListResponse>, t: Throwable) {
         Log.d("Response", "Failed to submit transaction")
         call.cancel()
     }
 })*/



}
