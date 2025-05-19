package com.inity.tickenity.domain.concert.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

	private String title;
	private String ageRating;
	private int duration;
	private String genre;
	private String description;
	private String postUrl;

	public Concert(String title, String ageRating, int duration, String genre, String description, String postUrl) {
		this.title = title;
		this.ageRating = ageRating;
		this.duration = duration;
		this.genre = genre;
		this.description = description;
		this.postUrl = postUrl;
	}
}
