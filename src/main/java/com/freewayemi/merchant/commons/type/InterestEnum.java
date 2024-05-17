package com.freewayemi.merchant.commons.type;

public enum InterestEnum {
    PER_TWELVE(12.0f),
    PER_TWELVE_POINT_FIVE(12.5f),
    PER_TWELVE_POINT_NINE_NINE(12.99f),
    PER_THIRTEEN(13.0f),
    PER_THIRTEEN_POINT_FIVE(13.5f),
    PER_THIRTEEN_POINT_NINE_NINE(13.99f),
    PER_FOURTEEN(14.0f),
    PER_FOURTEEN_POINT_NINE_NINE(14.99f),
    PER_FIFTEEN(15.0f),
    PER_SIXTEEN(16.0f),
    PER_SIXTEEN_POINT_FIVE(16.5f),
    PER_SEVENTEEN(17.0f),
    PER_SEVENTEEN_POINT_ZERO_ONE(17.01f),
    PER_EIGHTEEN(18.0f),
    PER_TWENTY(20.0f),
    PER_TWENTY_TWO(22.0f),
    PER_TWENTY_EIGHT(28.0f),
    PER_THIRTEEN_POINT_SEVEN_FIVE_TWO(13.752f),
    PER_FOURTEEN_POINT_SIX_FOUR(14.640f),
    PER_FOURTEEN_POINT_FOUR_TWO_SEVEN(14.427f),
    PER_FOURTEEN_POINT_NINE_NINE_TWO(14.992f),
    PER_FOURTEEN_POINT_NINE_NINE_SEVEN(14.997f),
    PER_FOURTEEN_POINT_NINE_NINE_THREE(14.993f),
    PER_FOURTEEN_POINT_EIGHT_ZERO_SIX(14.806f),
    PER_FOURTEEN_POINT_TWO_FOUR_ZERO(14.240f),
    PER_FIFTEEN_POINT_EIGHT_THREE(15.830f),
    PER_FIFTEEN_POINT_TWO_SIX_SEVEN(15.267f),
    PER_FIFTEEN_POINT_ONE_ZERO_SEVEN(15.107f),
    PER_ELEVEN_POINT_EIGHT_EIGHT(11.88f), PER_FIFTEEN_POINT_NINE_NINE(15.99f),
    PER_NINETEEN(19f);

    private Float interest;

    InterestEnum(Float interest) {
        this.interest = interest;
    }

    public Float getInterest() {
        return interest;
    }
}