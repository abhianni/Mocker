package com.mmt.wiremock.service;


import org.springframework.stereotype.Service;

@Service
public class PaymentMockedService {

    public String getPaymentHistoryResponse(){
        String response="{\n" +
                "  \"bookingDetailsList\": [\n" +
                "    {\n" +
                "      \"bookingId\": \"NH72008100444024\",\n" +
                "      \"bookingInfoList\": [\n" +
                "        {\n" +
                "          \"address\": \"\",\n" +
                "          \"amount\": 616.0,\n" +
                "          \"authCode\": \"1080523913\",\n" +
                "          \"billCity\": \"\",\n" +
                "          \"billCountry\": \"\",\n" +
                "          \"billFName\": \"\",\n" +
                "          \"billPinCode\": \"\",\n" +
                "          \"billState\": \"\",\n" +
                "          \"cardHolderName\": \"\",\n" +
                "          \"cardNo\": \"WL2018W04vVHX3Vr14rNW2+y6bTg==\",\n" +
                "          \"cardType\": \"\",\n" +
                "          \"ccHash\": \"\",\n" +
                "          \"channelProduct\": \"Native_Hotel\",\n" +
                "          \"currCode\": \"INR\",\n" +
                "          \"descErrorMessage\": \"Payment Success\",\n" +
                "          \"email\": \"test@test.com\",\n" +
                "          \"errorMsg\": \"Payment Success\",\n" +
                "          \"mobile\": \"3738232392\",\n" +
                "          \"mobileHash\": \"IJ6EUGUXP25\",\n" +
                "          \"name\": \"\",\n" +
                "          \"navUpdated\": \"N\",\n" +
                "          \"partPayment\": \"N\",\n" +
                "          \"payId\": \"NH72008100444024P01\",\n" +
                "          \"payMode\": \"My Wallet\",\n" +
                "          \"payStat\": \"Y\",\n" +
                "          \"paymentGateway\": \"MyWallet\",\n" +
                "          \"paymode\": \"My Wallet\",\n" +
                "          \"pgBankId\": \"My Wallet\",\n" +
                "          \"pgRefrenceId\": \"\",\n" +
                "          \"pgTxnId\": \"1080523913\",\n" +
                "          \"pyiExpMonth\": \"\",\n" +
                "          \"pyiExpYear\": \"\",\n" +
                "          \"pyiPGRRNo\": \"MWN\",\n" +
                "          \"pyiTraceId\": \"NH72008100444024P01\",\n" +
                "          \"pyipgSchRg\": \"0.0\",\n" +
                "          \"requestDate\": \"Dec 18, 2018 7:44:11 AM\",\n" +
                "          \"shipCity\": \"\",\n" +
                "          \"shipCountry\": \"\",\n" +
                "          \"shipFName\": \"\",\n" +
                "          \"shipPinCode\": \"\",\n" +
                "          \"transactionId\": \"1080523913\",\n" +
                "          \"type\": \"LOB\",\n" +
                "          \"uatpFlag\": \"N\",\n" +
                "          \"uuid\": \"UCLP0E0RGGH\",\n" +
                "          \"walletSplit\": {\n" +
                "            \"WalletNo\": \"WL2018121765882679\",\n" +
                "            \"WalletTransactionDetails\": [\n" +
                "              {\n" +
                "                \"Date\": \"2018-12-18T07:46:32\",\n" +
                "                \"LOBCode\": \"LOB02710\",\n" +
                "                \"TotalAmount\": 616.0,\n" +
                "                \"TransactionID\": \"1080523913\",\n" +
                "                \"UniqueID\": \"NH72008100444024\",\n" +
                "                \"Wallets\": [\n" +
                "                  {\n" +
                "                    \"amount\": 0.0,\n" +
                "                    \"name\": \"real\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                    \"amount\": 0.0,\n" +
                "                    \"name\": \"plus\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                    \"amount\": 616.0,\n" +
                "                    \"name\": \"bonus\",\n" +
                "                    \"types\": [\n" +
                "                      {\n" +
                "                        \"amount\": 100.0,\n" +
                "                        \"code\": \"APPINSTANTCREDIT\"\n" +
                "                      },\n" +
                "                      {\n" +
                "                        \"amount\": 516.0,\n" +
                "                        \"code\": \"APPinstall\"\n" +
                "                      }\n" +
                "                    ]\n" +
                "                  }\n" +
                "                ]\n" +
                "              }\n" +
                "            ]\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          \"address\": \"\",\n" +
                "          \"amount\": 9095.0,\n" +
                "          \"authCode\": \"880662\",\n" +
                "          \"billCity\": \"\",\n" +
                "          \"billCountry\": \"\",\n" +
                "          \"billFName\": \"\",\n" +
                "          \"billPinCode\": \"\",\n" +
                "          \"billState\": \"\",\n" +
                "          \"cardHolderName\": \"N SRINIVASAN\",\n" +
                "          \"cardNo\": \"437551XXXXXX3003\",\n" +
                "          \"cardType\": \"VISA\",\n" +
                "          \"ccHash\": \"PNTWX/ARLVzkkB0H5y2FZ0PSrEBWaQUwKmFw9vY+7oKosr7PbFwGA1QJ2IzrXsqdE2S0E/Zv5GOHsI0o0af2LQ==\",\n" +
                "          \"channelProduct\": \"Native_Hotel\",\n" +
                "          \"currCode\": \"INR\",\n" +
                "          \"descErrorMessage\": \"Payment Success\",\n" +
                "          \"email\": \"test@test.com\",\n" +
                "          \"errorMsg\": \"Payment Success\",\n" +
                "          \"mobile\": \"3738232392\",\n" +
                "          \"mobileHash\": \"IJ6EUGUXP25\",\n" +
                "          \"name\": \"\",\n" +
                "          \"navUpdated\": \"N\",\n" +
                "          \"partPayment\": \"N\",\n" +
                "          \"payId\": \"NH72008100444024P2332929\",\n" +
                "          \"payMode\": \"Credit Card\",\n" +
                "          \"payStat\": \"Y\",\n" +
                "          \"paymentGateway\": \"ICICIHMIGS\",\n" +
                "          \"paymode\": \"Credit Card\",\n" +
                "          \"pgBankId\": \"ICICI Bank\",\n" +
                "          \"pgRefrenceId\": \"2090005354\",\n" +
                "          \"pgTxnId\": \"2090005354237828\",\n" +
                "          \"pyiExpMonth\": \"06\",\n" +
                "          \"pyiExpYear\": \"2022\",\n" +
                "          \"pyiPGRRNo\": \"835213631986272\",\n" +
                "          \"pyiTraceId\": \"NH72008100444024P2332929\",\n" +
                "          \"pyipgSchRg\": \"0.0\",\n" +
                "          \"requestDate\": \"Dec 18, 2018 7:45:54 AM\",\n" +
                "          \"shipCity\": \"\",\n" +
                "          \"shipCountry\": \"\",\n" +
                "          \"shipFName\": \"\",\n" +
                "          \"shipPinCode\": \"\",\n" +
                "          \"transactionId\": \"2090005354\",\n" +
                "          \"type\": \"LOB\",\n" +
                "          \"uatpFlag\": \"N\",\n" +
                "          \"uuid\": \"UXDMR46Z1MB\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ],\n" +
                "  \"imintDetail\": [],\n" +
                "  \"status\": \"SUCCESS\"\n" +
                "}\n";

        return response;
    }
}
