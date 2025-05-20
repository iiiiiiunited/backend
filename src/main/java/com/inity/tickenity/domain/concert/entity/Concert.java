package com.inity.tickenity.domain.concert.entity;

import java.util.ArrayList;
import java.util.List;

import com.inity.tickenity.domain.concert.enums.Genre;
import com.inity.tickenity.domain.concertvenue.ConcertVenue;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "concerts")
@NoArgsConstructor
public class Concert {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false, name = "age_rating")
	private String ageRating;

	@Column(nullable = false)
	private int duration;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Genre genre;

	private String description;

	@Column(nullable = false, name = "post_url")
	private String postUrl;

	public Concert(String title, String ageRating, int duration, Genre genre, String description, String postUrl) {
		this.title = title;
		this.ageRating = ageRating;
		this.duration = duration;
		this.genre = genre;
		this.description = description;
		this.postUrl = postUrl;
	}
}
