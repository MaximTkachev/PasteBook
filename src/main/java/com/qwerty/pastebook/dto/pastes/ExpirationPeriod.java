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
        switch (text) {
            case "10m":
                return ExpirationPeriod.TenMinutes;
            case "1h":
                return ExpirationPeriod.OneHour;
            case "3h":
                return ExpirationPeriod.ThreeHours;
            case "1d":
                return ExpirationPeriod.OneDay;
            case "1w":
                return ExpirationPeriod.OneWeek;
            case "N":
                return ExpirationPeriod.Eternal;
            default:
                throw new BadRequestException("unknown expiration period");
        }
    }
}
