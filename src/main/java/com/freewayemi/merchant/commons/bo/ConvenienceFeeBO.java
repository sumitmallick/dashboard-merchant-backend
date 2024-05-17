package com.freewayemi.merchant.commons.bo;

import com.freewayemi.merchant.commons.bo.brms.Input;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.commons.dto.ConvFeeRate;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ConvenienceFeeBO {

    public Float calculate(Float txnAmount, Input input) {
        if (null == input || null == input.getConvFeeRates() || 0 == input.getConvFeeRates().size()) {
            return 0.0f;
        }

        for (ConvFeeRate convFeeRate : input.getConvFeeRates()) {
            if (input.getBankCode().equals(convFeeRate.getBankCode()) &&
                    input.getCardType().equals(convFeeRate.getCardType()) &&
                    input.getTenure().equals(convFeeRate.getTenure())) {
                return null != convFeeRate.getType() && "flat".equals(convFeeRate.getType()) ? convFeeRate.getRate() :
                        null != convFeeRate.getType() && "tataaig".equals(convFeeRate.getType()) ? convFeeForTataAig(txnAmount) :
                                (convFeeRate.getRate() * txnAmount) / 100;
            }
        }

        List<ConvFeeRate> out = input.getConvFeeRates().stream()
                .map(cFeeRate -> cFeeRate.setScore(getScore(cFeeRate, input))).collect(Collectors.toList())
                .stream().sorted(Comparator.comparingInt(ConvFeeRate::getScore).reversed())
                .map(cFeeRate -> new ConvFeeRate(
                        null == cFeeRate.getTenure() || cFeeRate.getTenure().equals(-1) ? input.getTenure() :
                                cFeeRate.getTenure(),
                        cFeeRate.getRate(),
                        null == cFeeRate.getCardType() ? input.getCardType() : cFeeRate.getCardType(),
                        null == cFeeRate.getBankCode() ? input.getBankCode() : cFeeRate.getBankCode(),
                        cFeeRate.getType()
                ))
                .filter(cFeeRate -> (null == cFeeRate.getCardType() && null == input.getCardType()) ||
                        cFeeRate.getCardType().equals(input.getCardType()))
                .filter(cFeeRate -> (null == cFeeRate.getBankCode() && null == input.getBankCode()) ||
                        cFeeRate.getBankCode().equals(input.getBankCode()))
                .filter(cFeeRate -> (null == cFeeRate.getTenure() && null == input.getTenure()) ||
                        cFeeRate.getTenure().equals(input.getTenure()))
                .collect(Collectors.toList());

        if (out.isEmpty()) {
            return 0.0f;
        }

        return null != out.get(0).getType() && "flat".equals(out.get(0).getType()) ? out.get(0).getRate() :
                null != out.get(0).getType() && "tataaig".equals(out.get(0).getType()) ? convFeeForTataAig(txnAmount) :
                        (out.get(0).getRate() * txnAmount) / 100;
    }

    private Integer getScore(ConvFeeRate convFeeRate, Input input) {
        int score = 0;
        if (input.getTenure().equals(convFeeRate.getTenure()))
            score += 1;
        if (input.getCardType().equals(convFeeRate.getCardType()))
            score += 1;
        if (input.getBankCode().equals(convFeeRate.getBankCode()))
            score += 1;
        return score;
    }

    private Float convFeeForTataAig(Float txnAmount) {
        if (txnAmount > Util.getFLoat(70000f)) {
            return Util.getFLoat(349f);
        } else if (txnAmount >= Util.getFLoat(40001f) && txnAmount <= Util.getFLoat(70000f)) {
            return Util.getFLoat(299f);
        } else if (txnAmount >= Util.getFLoat(20001f) && txnAmount <= Util.getFLoat(40000f)) {
            return Util.getFLoat(199f);
        } else if (txnAmount >= Util.getFLoat(10001f) && txnAmount <= Util.getFLoat(20000f)) {
            return Util.getFLoat(99f);
        } else
            return Util.getFLoat(49f);
    }
}
