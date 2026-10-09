# VeloVerse image credits

Last updated: 9 October 2026

## Images currently used on the website

No third-party photographs are used yet. Every image on the site is an original, simple
vector drawing written as SVG code inside this project. They are not photographs, they were
not produced by an image-generation model, and they show no brands, logos or people.

| File | Where it appears | What it is | Licence |
|---|---|---|---|
| `src/main/resources/static/images/bike-hero.svg` | Homepage hero and "Anatomy of a Bicycle" section | Line illustration of a generic road bicycle with no brand marks | Original to the VeloVerse project |
| `src/main/resources/static/images/placeholder-bike.svg` | Placeholder on list, detail and model cards | Neutral bicycle outline marking where a photograph will go | Original to the VeloVerse project |
| Inline SVG icons in `templates/home.html` | Homepage "What to Explore" cards | Simple line icons (bicycle, gear, cube, tag) | Original to the VeloVerse project |

The bicycle anatomy diagram on the Components page is inline SVG in
`templates/components/overview.html`, also original to the project.

## Photographs proposed but NOT used

These six photographs were shortlisted on 9 October 2026. Each original photo page was checked
and showed the free licence ("Free to use under the Unsplash License" or "Free" under the
Pexels License); none is an Unsplash+ or paid image.

They were **not downloaded or used**, because the development environment could not download
images from Unsplash or Pexels. As a result, the images themselves have **not** been checked for
logos, decals, people or resolution. Before adding any of them, open the original page, look at
the full image, and complete the checks in the last column.

| Intended section | Photo page | Photographer | Licence | Still to check |
|---|---|---|---|---|
| Homepage hero | https://unsplash.com/photos/grayscale-photography-of-road-bicycle-pIwu5XNvXpk | Josh Nuttall (https://unsplash.com/@jjnuttall) | Unsplash License | Frame decals; wide enough for a full-width hero |
| Explore: Bicycle Types | https://unsplash.com/photos/bicycles-leaning-on-wall-ui-7qNojNSY | Chi Yim (https://unsplash.com/@yim90117) | Unsplash License | Tags mention "motorcycle": check for motorbikes, number plates and people |
| Explore: Components | https://www.pexels.com/photo/close-up-shot-of-a-bicycle-gears-4530854/ | Harvey Tan Villarino (https://www.pexels.com/@harveyvillarino/) | Pexels License | Manufacturer names printed on parts |
| Explore: Materials | https://unsplash.com/photos/black-road-bike-on-against-black-background-eTrHMJwI5ro | Josh Nuttall (https://unsplash.com/@jjnuttall) | Unsplash License | Frame decals |
| Explore: Brands | https://unsplash.com/photos/black-road-bicycle-handle-with-black-background-XVTWFHcNIko | Josh Nuttall (https://unsplash.com/@jjnuttall) | Unsplash License | Logos on the bar or stem |
| Anatomy of a Bicycle | https://www.pexels.com/photo/bicycle-bike-repair-shop-132682/ | Alexander Dummer (https://www.pexels.com/@alexander-dummer-37646/) | Pexels License | Tool or workstand logos in the background |

## Licence summaries (checked 9 October 2026)

- **Unsplash License** – https://unsplash.com/license
  Free for commercial and non-commercial use. No permission or attribution required (credit is
  appreciated). Does not allow compiling Unsplash photos to build a similar or competing service.
  Unsplash+ images are covered by a different, paid licence and must not be used.
- **Pexels License** – https://www.pexels.com/license/
  Free to use, including commercially. Attribution not required (appreciated). Not allowed:
  implying that people or brands shown endorse the site, using a photo as part of a trademark,
  selling unaltered copies, or redistributing photos on other stock or wallpaper platforms.

Even though credit is optional, VeloVerse should credit every photographer here once a photo is
added.

## Rules for adding a photograph later

1. Use only a photo whose own page shows the free Unsplash or Pexels licence (not Unsplash+).
2. Look at the full image: no readable logos or decals, no identifiable people, and it really
   shows the intended subject.
3. Never use a generic bicycle photo on a card for a specific real model (for example
   "Trek 520 Disc"). Keep the neutral placeholder until an accurate, licensed photo of that model
   is available.
4. Resize before adding (around 1600–2400 px wide for the hero, 1000 px for cards) and save in
   `src/main/resources/static/images/photos/`.
5. Add a row to the "Images currently used" table with the photographer, the original URL, the
   licence URL and whether attribution is required.
