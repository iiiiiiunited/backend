package com.inity.tickenity.domain.concertvenue;

import com.inity.tickenity.domain.concert.entity.Concert;
import com.inity.tickenity.domain.venue.entity.Venue;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class ConcertVenue {
	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "concert_id")
	private Concert concert;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "venue_id")
	private Venue venue;

	public ConcertVenue(Concert concert, Venue venue) {
		this.concert = concert;
		this.venue = venue;
	}
}
