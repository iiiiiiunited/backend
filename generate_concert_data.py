#!/usr/bin/env python3
import csv
import random
import argparse
from faker import Faker

def generate_csv(file_path: str, num_records: int):
    fake = Faker()
    # 실제 프로젝트의 Genre enum 값으로 수정하세요
    age_ratings = ["ALL", "12+", "15+", "18+"]
    genres = ["POP", "ROCK", "JAZZ", "CLASSICAL", "HIPHOP", "EDM"]

    with open(file_path, 'w', newline='', encoding='utf-8') as csvfile:
        writer = csv.writer(csvfile)
        # CSV 헤더 (id는 DB에서 AUTO_INCREMENT 처리)
        writer.writerow(['title', 'age_rating', 'duration', 'genre', 'description', 'post_url'])

        for i in range(1, num_records + 1):
            title       = fake.sentence(nb_words=4).rstrip('.')
            age_rating  = random.choice(age_ratings)
            duration    = random.randint(60, 180)
            genre       = random.choice(genres)
            description = fake.text(max_nb_chars=100)
            post_url    = fake.url()

            writer.writerow([title, age_rating, duration, genre, description, post_url])

            # 진행 로그 (선택)
            if i % 100_000 == 0:
                print(f"{i:,} records written")

    print(f"✔ CSV generation complete: {file_path} ({num_records:,} rows)")

if __name__ == "__main__":
    parser = argparse.ArgumentParser(
        description="Generate dummy Concert CSV data")
    parser.add_argument(
        '--output', '-o',
        type=str,
        default='concert_data.csv',
        help='Output CSV file path')
    parser.add_argument(
        '--num', '-n',
        type=int,
        default=1_000_000,
        help='Number of records to generate')
    args = parser.parse_args()

    generate_csv(args.output, args.num)
