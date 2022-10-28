package com.money.me.motivate.domain;

import com.money.me.motivate.settings.Complexity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appuser_id")
    private AppUser user;

    private String description;
    private Complexity complexity;
    private Date createdDate = new Date();
    private boolean completed = false;
    private Double receivedAward;
}
