package com.money.me.motivate.domain.modifiers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class ModifiersSet {
    private Double coinsTaskModifier;
    private Double coinsPerHour;
}
