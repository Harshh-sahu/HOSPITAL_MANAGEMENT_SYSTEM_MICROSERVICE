package com.hms.profile.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Patient blood group", allowableValues = {
        "A_POSITIVE", "A_NEGATIVE", "B_POSITIVE", "B_NEGATIVE",
        "AB_POSITIVE", "AB_NEGATIVE", "O_POSITIVE", "O_NEGATIVE"
})
public enum BloodGroup {

    A_POSITIVE,
    A_NEGATIVE,
    B_POSITIVE,
    B_NEGATIVE,
    AB_POSITIVE,
    AB_NEGATIVE,
    O_POSITIVE,
    O_NEGATIVE;
}
