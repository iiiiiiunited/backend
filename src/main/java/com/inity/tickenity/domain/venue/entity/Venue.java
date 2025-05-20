package com.inity.tickenity.domain.venue.entity;

import java.util.ArrayList;
import java.util.List;

import com.inity.tickenity.domain.concert.entity.Concert;
import com.inity.tickenity.domain.concertvenue.ConcertVenue;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "venues")
@Getter
@RequiredArgsConstructor
public class Venue {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String address;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private int capacity;

	@Column(nullable = false)
	private String description;

	@OneToMany(mappedBy = "venue", fetch = FetchType.LAZY)
	private List<ConcertVenue> concertVenues = new ArrayList<>();

	public Venue(String address, String name, int capacity, String description) {
		this.address = address;
		this.name = name;
		this.capacity = capacity;
		this.description = description;
	}
}
