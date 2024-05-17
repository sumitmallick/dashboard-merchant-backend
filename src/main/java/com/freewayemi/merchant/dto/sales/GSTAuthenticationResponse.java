package com.freewayemi.merchant.dto.sales;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

@Data
@JsonDeserialize(builder = GSTAuthenticationResponse.GstAuthenticationResponseBuilder.class)
@Builder(builderClassName = "GstAuthenticationResponseBuilder", toBuilder = true)
public class GSTAuthenticationResponse {
    @JsonProperty("result")
    private final Result result;

    @JsonProperty("requestId")
    private final String requestId;

    @JsonProperty("statusCode")
    private final String statusCode;

    public GSTAuthenticationResponse(Result result, String requestId, String statusCode) {
        this.result = result;
        this.requestId = requestId;
        this.statusCode = statusCode;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class GstAuthenticationResponseBuilder {
    }

    @Data
    @JsonDeserialize(builder = Result.ResultBuilder.class)
    @Builder(builderClassName = "ResultBuilder", toBuilder = true)
    public static class Result {

        @JsonProperty("canFlag")
        private final String canFlag;

        @JsonProperty("contacted")
        private final Contacted contacted;

        @JsonProperty("aggreTurnOver")
        private final String aggreTurnOver;

        @JsonProperty("ppr")
        private final String cmpRt;

        @JsonProperty("rgdt")
        private final String rgdt;

        @JsonProperty("tradeNam")
        private final String tradeNam;

        @JsonProperty("nba")
        private final String[] nba;

        @JsonProperty("mbr")
        private final String[] mbr;

        @JsonProperty("adadr")
        private final Adara[] adara;

        @JsonProperty("pradr")
        private final Pradr pradr;

        @JsonProperty("mandatedeInvoice")
        private final String mandatedeInvoice;

        @JsonProperty("stjCd")
        private final String stjCd;

        @JsonProperty("lstupdt")
        private final String lstupdt;

        @JsonProperty("gstin")
        private final String gstin;

        @JsonProperty("bzsdtls")
        private final Bzsdtls[] bzsdtls;

        @JsonProperty("ctjCd")
        private final String ctjCd;

        @JsonProperty("bzgddtls")
        private final Bzgddtls[] bzgddtls;

        @JsonProperty("stj")
        private final String stj;

        @JsonProperty("dty")
        private final String dty;

        @JsonProperty("cxdt")
        private final String cxdt;

        @JsonProperty("ctb")
        private final String ctb;

        @JsonProperty("sts")
        private final String sts;

        @JsonProperty("lgnm")
        private final String lgnm;

        @JsonPOJOBuilder(withPrefix = "")
        public static class ResultBuilder {
        }

        @Data
        @JsonDeserialize(builder = Contacted.ContactedBuilder.class)
        @Builder(builderClassName = "ContactedBuilder", toBuilder = true)
        public static class Contacted {

            @JsonProperty("mobNum")
            private final long mobNum;

            @JsonProperty("email")
            private final String email;

            @JsonProperty("name")
            private final String name;

            @JsonPOJOBuilder(withPrefix = "")
            public static class ContactedBuilder {
            }

        }

        @Data
        @JsonDeserialize(builder = Adara.AdaraBuilder.class)
        @Builder(builderClassName = "AdaraBuilder", toBuilder = true)
        public static class Adara {

            @JsonProperty("em")
            private final String em;

            @JsonProperty("adr")
            private final String adr;

            @JsonProperty("addr")
            private final String addr;

            @JsonProperty("mb")
            private final String mb;

            @JsonProperty("ntr")
            private final String ntr;

            @JsonProperty("lastUpdatedDate")
            private final String lastUpdatedDate;

            @JsonPOJOBuilder(withPrefix = "")
            public static class AdaraBuilder {
            }

        }

        @Data
        @JsonDeserialize(builder = Pradr.PradrBuilder.class)
        @Builder(builderClassName = "PradrBuilder", toBuilder = true)
        public static class Pradr {

            @JsonProperty("em")
            private final String em;

            @JsonProperty("ntr")
            private final String ntr;

            @JsonProperty("adr")
            private final String adr;

            @JsonProperty("addr")
            private final String addr;

            @JsonProperty("mb")
            private final long mb;

            @JsonProperty("lastUpdatedDate")
            private final String lastUpdatedDate;

            @JsonPOJOBuilder(withPrefix = "")
            public static class PradrBuilder {
            }

        }

        @Data
        @JsonDeserialize(builder = Bzsdtls.BzsdtlsBuilder.class)
        @Builder(builderClassName = "BzsdtlsBuilder", toBuilder = true)
        public static class Bzsdtls {
            @JsonProperty("sdes")
            private final String sdes;

            @JsonProperty("saccd")
            private final String saccd;

            @JsonPOJOBuilder(withPrefix = "")
            public static class BzsdtlsBuilder {
            }
        }

        @Data
        @JsonDeserialize(builder = Bzgddtls.BzgddtlsBuilder.class)
        @Builder(builderClassName = "BzgddtlsBuilder", toBuilder = true)
        public static class Bzgddtls {
            @JsonProperty("hsncd")
            private final String hsncd;

            @JsonProperty("gdes")
            private final String gdes;

            @JsonPOJOBuilder(withPrefix = "")
            public static class BzgddtlsBuilder {
            }
        }
    }

}
