package com.qwerty.pastebook.dto.pastes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.qwerty.pastebook.exceptions.BadRequestException;

import java.util.Optional;

public enum ExpirationPeriod {
    TenMinutes(Optional.of(600_000)),
    OneHour(Optional.of(3_600_600)),
    ThreeHours(Optional.of(10_800_000)),
    OneDay(Optional.of(86_400_000)),
    OneWeek(Optional.of(604_800_000)),
    Eternal(Optional.empty());

    public final Optional<Integer> duration;
    public final Boolean eternal;

    ExpirationPeriod(Optional<Integer> duration) {
        this.duration = duration;
        this.eternal = duration.isEmpty();
    }


    @Override
    public String toString() {
        return super.toString();
    }

    @JsonCreator
    public static ExpirationPeriod fromText(String text) {
        if (text.equals("10m") || text.equals("TenMinutes"))
            return ExpirationPeriod.TenMinutes;
        if (text.equals("1h") || text.equals("OneHour"))
            return ExpirationPeriod.OneHour;
        if (text.equals("3h") || text.equals("ThreeHours"))
            return ExpirationPeriod.ThreeHours;
        if (text.equals("1d") || text.equals("OneDay"))
            return ExpirationPeriod.OneDay;
        if (text.equals("1w") || text.equals("OneWeek"))
            return ExpirationPeriod.OneWeek;
        if (text.equals("N") || text.equals("Eternal"))
            return ExpirationPeriod.Eternal;

        throw new BadRequestException("unknown expiration period");
    }
}
