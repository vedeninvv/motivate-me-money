package com.money.me.motivate.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "appuser_modifiers_set")
@Getter
@Setter
@RequiredArgsConstructor
public class AppUserModifiersSet extends ModifiersSet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
