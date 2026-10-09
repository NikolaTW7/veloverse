# UI Design Version 2 - VeloVerse

## 1. Purpose

This document describes the Version 2 user interface of VeloVerse (originally planned as BikePedia) as it is actually implemented in the application.

Version 1 followed the original wireframes in the `BikePedia-Wireframes` folder: a light design with white cards, grey sections and blue accents. Version 2 keeps the same pages, URLs, content and features, and replaces the visual design with a dark, cinematic style.

The original design documentation (`Docs/BikePedia_Project_Documentation.docx`, `Docs/database-design.md` and the wireframes) is unchanged. This file only documents the Version 2 interface.

Version 2 covers every existing page:

- Home
- Bicycle Types and Bicycle Type Detail
- Components & Anatomy and Component Detail
- Materials and Material Detail
- Brands and Brand Detail
- Bicycles (with Search and Filters) and Bicycle Detail
- Compare Bicycles
- Custom 404 page and general error page
- Shared header and footer

## 2. Design Concept and Objectives

The concept is a **premium, dark, cinematic cycling website** that is still clearly an **independent educational resource**, not a shop.

Objectives:

1. Give the site a strong, modern identity instead of a plain wireframe look.
2. Keep the educational content easy to read, using high-contrast text on dark surfaces.
3. Use one shared design system so every page looks consistent.
4. Keep all existing functionality, URLs and data unchanged.
5. Stay fast and simple: no JavaScript, no external fonts, no extra dependencies.
6. Be honest with images: use real, licensed photographs only when they have been verified, and never show an unrelated photo as a specific real bicycle model.

The style is inspired by premium cycling websites in general, but the layout, artwork and text are original. No brand's design, logo or assets are copied.

## 3. Colour Palette

All colours are defined once as CSS variables in `:root` at the top of `src/main/resources/static/css/styles.css`. Components use the variables, not hard-coded colours.

| Token | HEX / value | Used for |
|---|---|---|
| `--color-bg` | `#0A0A0B` | Page background (near-black) |
| `--color-bg-light` | `#111113` | Alternate section background |
| `--color-surface` | `#161618` | Cards, panels, form fields |
| `--color-surface-2` | `#1D1D20` | Raised elements inside cards, placeholder images, table header, component pills |
| `--color-panel` | `#18181B` | "Part of the … system" panel on component detail pages |
| `--color-footer` | `#050506` | Footer and "Pros & Cons" sections |
| `--color-text` | `#F5F5F4` | Headings and main text |
| `--color-muted` | `#A1A1AA` | Secondary text, labels, breadcrumbs |
| `--color-footer-text` | `#A1A1AA` | Footer text |
| `--color-border` | `#26262B` | Card and section borders |
| `--color-border-strong` | `#3A3A41` | Form fields, hover borders |
| `--color-accent` | `#C8F031` | Lime accent: primary buttons, active navigation, focus outline, small labels |
| `--color-accent-hover` | `#D9FF5A` | Primary button hover |
| `--color-accent-soft` | `rgba(200, 240, 49, 0.12)` | Lime tint behind icons and tags |
| `--color-on-accent` | `#0A0A0B` | Text placed on lime |
| `--color-danger` | `#F87171` | Validation messages (Compare page) |

Other fixed values:

- Lead paragraphs use `#C4C4CC`, slightly brighter than the muted grey.
- The footer note uses `#8B8B93`.

**Rule for the accent:** lime is used sparingly. It appears on primary buttons, the active menu item, keyboard focus, numbers and small "eyebrow" labels. It is never used for paragraphs.

### Contrast (WCAG 2.1)

| Text | Background | Ratio | Result |
|---|---|---|---|
| `#F5F5F4` main text | `#0A0A0B` | 18.1 : 1 | AAA |
| `#A1A1AA` muted text | `#0A0A0B` | 7.7 : 1 | AAA |
| `#A1A1AA` muted text | `#161618` (card) | 7.1 : 1 | AAA |
| `#C4C4CC` lead text | `#0A0A0B` | 11.4 : 1 | AAA |
| `#C8F031` lime text / links | `#0A0A0B` | 15.0 : 1 | AAA |
| `#0A0A0B` button text | `#C8F031` lime button | 15.0 : 1 | AAA |
| `#8B8B93` footer note | `#050506` | 6.0 : 1 | AA |
| `#F87171` danger border/text | `#0A0A0B` | 7.2 : 1 | AAA |

## 4. Typography and Spacing

### 4.1 Typography

- **Font:** the system font stack (`system-ui, -apple-system, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif`). No web fonts are downloaded, so text appears immediately and no third-party font service is contacted.
- **Headings:** weight 800, tight letter spacing (`-0.02em`; `-0.035em` for `h1`), white.
- **Body text:** line height 1.6.
- **Eyebrow labels:** small uppercase labels above headings, `0.75rem`, weight 600, letter spacing `0.14em`, lime or grey.

| Element | Size |
|---|---|
| Homepage hero headline | `clamp(3.2rem, 8.5vw, 7rem)`, line height 0.95 |
| Inner page `h1` | `clamp(2.6rem, 6vw, 4.75rem)`, line height 1.02 |
| Section titles | `clamp(2rem, 3.6vw, 3rem)` |
| Compare promotion heading | `clamp(2rem, 4vw, 3.25rem)` |
| 404 number | `clamp(6rem, 22vw, 13rem)`, outlined |
| Lead paragraph | `1.15rem` (`1.2rem` in the hero) |

`clamp()` lets headings scale smoothly between phone and desktop without extra breakpoints.

### 4.2 Spacing and layout

| Setting | Value |
|---|---|
| Content width | `1232px` maximum (`--container`) |
| Page side padding | `24px` (`16px` on phones) |
| Section padding | `80px` top and bottom (`56px` on phones) |
| Card grid gap | `24px` (`20px` for Explore cards) |
| Corner radius | `14px` (`--radius`); `24px` for large panels such as the Compare promotion |
| Large shadow | `0 24px 60px rgba(0, 0, 0, 0.55)` (`--shadow`) |

## 5. Homepage Sections

The homepage (`templates/home.html`) has seven parts, top to bottom.

| # | Section | Content |
|---|---|---|
| 1 | **Cinematic hero** | Full-width dark stage with a lime glow and a large original bicycle illustration on the right. Left side: eyebrow "An independent bicycle knowledge base", the headline "Understand **Your Bike**" ("Your Bike" in lime), the intro sentence, and two buttons, **Explore Bicycles** (lime) and **Explore Components** (outline). A gradient behind the text keeps it readable over the artwork. |
| 2 | **Quick links bar** | Along the bottom of the hero: Types, Components, Materials, Brands, Compare, each with an arrow. |
| 3 | **What to Explore** | Four clean, text-only cards numbered 01-04 in lime: Bicycle Types, Components & Anatomy, Materials, Brands. Each has a heading, a short description and an "Explore …" link aligned to the bottom of the card. |
| 4 | **Bicycle Types** | The first four bicycle types from the database, each with a placeholder image, description (three lines maximum) and "Learn more" link, plus "View all bicycle types". |
| 5 | **Anatomy of a Bicycle** | The bicycle illustration beside four numbered systems: Drivetrain, Brakes, Wheels & Tyres, Frame & Cockpit. Then an "Explore Components" button. |
| 6 | **Featured Models** | The first three bicycles from the database: brand, model, "type · frame material · year", and "Learn more". The cards deliberately use neutral placeholders labelled "Photo to be added", so no image can be mistaken for the real model. |
| 7 | **Compare promotion** | A large panel, "Compare two bicycles side by side", explaining that the comparison is neutral (no scores, no winner), with a **Compare Bicycles** button and a simple two-column graphic. |

The homepage data still comes from the existing controller (`featuredTypes`, `featuredBicycles`). The redesign changed no Java code.

## 6. Layout of the Other Pages

All inner pages share the same building blocks, so they only needed the shared stylesheet.

### 6.1 Shared page structure

1. **Page header (`.page-hero`):** a dark stage with a soft lime glow, breadcrumb, eyebrow, large `h1`, lead paragraph and a placeholder image on the right (stacked on phones).
2. **Content sections:** alternating `#0A0A0B` and `#111113` backgrounds, separated by thin borders.
3. **Cards:** charcoal `#161618` with a subtle border.

### 6.2 Page by page

| Page | Layout |
|---|---|
| **Bicycle Types** | Header, then a 4-column grid of type cards (image, name, description, "Best for", Learn more). |
| **Bicycle Type Detail** | Header with a "Typical use" chip; "Pros & Cons" in a near-black section: two panels headed "Advantages" and "Disadvantages", each a plain text list separated by thin lines (no tick or cross symbols); example models; links to other types. |
| **Components & Anatomy** | Header; an interactive SVG anatomy diagram on a dark panel (white frame, grey parts, lime label dots, label boxes turn lime on hover/focus); cards for each component system; "How it works together" steps. |
| **Component Detail** | Header with system chip; "What does it do?" / "How does it work?" blocks with a lime left border; materials; related components; a dark charcoal (`#18181B`) "Part of the … system" panel with a thin lime top line, lime labels, a white system name, soft grey description, and pills for the system's components (the current component in lime, the others as grey outlined links). |
| **Materials / Material Detail** | 3-column material cards; detail page with Advantages & Disadvantages in the near-black section, as plain text lists like the Bicycle Type Detail page. |
| **Brands / Brand Detail** | 3-column brand cards with country; detail page with a "Country" chip and the brand's models in the same cards as the Bicycles page. |
| **Bicycles (Search and Filters)** | Search box with a lime Search button, then three dropdowns (Bicycle type, Brand, Frame material) and "Apply filters" / "Clear filters". A results line, then 3-column bicycle cards (brand, model, year / type / frame, description, Learn more). Friendly empty and "No bicycles found" messages. |
| **Bicycle Detail** | Header with full description; "Specifications" as a two-column list with links to the brand, type and material; "Not specified" for missing values. |
| **Compare Bicycles** | Two dropdowns and a lime Compare button; validation messages in a red-bordered panel; "Bicycle A / Bicycle B" cards with detail links; the side-by-side table (section 8.4); a neutral note under the table. |
| **404 page** | A very large outlined "404" in dark grey, the "Page not found" message, and buttons back to Home and Bicycle Types. |
| **General error page** | Same style as the 404 page. |

## 7. Header and Footer

Both are defined once in `templates/fragments/layout.html` and used by every page.

### 7.1 Header

- Logo: an original lime road-bike outline followed by "Velo" in white and "**Verse**" in lime. The bike is drawn as inline SVG (48 × 30 drawing shown at 44 × 27.5 px) with two wheels and hubs, a road frame (top tube, down tube, seat tube, seat and chain stays, head tube), a raked fork, saddle and seatpost, stem with drop handlebar, and a chainring with crank and pedal. The same logo fragment is used in the header and the footer.
- Main navigation: Home, Bicycle Types, Components, Bicycles, Materials, Brands, Compare.
- The current page is white, bold and underlined with a thin lime bar, and is marked `aria-current="page"` for screen readers.
- On screens wider than 1024px the header stays at the top while scrolling (semi-transparent with background blur). On smaller screens it is not sticky, so it never covers content.
- On tablets and phones the navigation wraps onto two lines (no hamburger menu and no JavaScript).
- A **"Skip to main content"** link appears when a keyboard user presses Tab first.

### 7.2 Footer

- Near-black (`#050506`) with a top border.
- Logo, a short description, and the note "No shop, no advertising and no tracking — just bicycle knowledge."
- Two link columns: **Navigation** (Home, Bicycle Types, Components, Bicycles) and **Resources** (Materials, Brands, Compare). Links turn lime on hover and focus.
- Bottom line: copyright with the current year, and "Educational project — not affiliated with any brand."

## 8. Buttons, Cards, Forms and Comparison Tables

### 8.1 Buttons

| Class | Look | Use |
|---|---|---|
| `.btn-dark` (primary) | Lime background, near-black text; brighter lime on hover | Main action: Explore Bicycles, Search, Compare |
| `.btn-outline` | Transparent with a white 35% border; border and text turn lime on hover | Secondary action |
| `.btn-light` | Charcoal with a border; lime border/text on hover | "Learn more" inside cards |
| `.text-link` | Lime text with an arrow that moves 4px on hover | Inline links |

All buttons rise by 1px on hover, unless reduced motion is requested. The class name `.btn-dark` was kept from Version 1 so no template had to change.

### 8.2 Cards

- **Type / brand / material / bicycle cards (`.type-tile`):** charcoal, 14px corners, placeholder image on top, eyebrow, title, three-line description, button at the bottom so cards line up. On hover they lift 3px and the border brightens.
- **Explore cards (homepage):** text-only gradient cards with a lime number, heading, description and a link aligned to the bottom; a lime border on hover or focus.
- **Model cards (homepage):** charcoal with a wide placeholder image and spec line.
- **Info blocks and panels:** charcoal with a lime left border (component detail). The system panel is a dark `#18181B` card with a 3px lime top line; lime is used only for its small labels and the current-component pill.
- **Component pills:** other components are `#1D1D20` pills with a grey border and light grey text that turn lime on hover or focus; the current component has a lime border, a faint lime tint, bold lime text and `aria-current="page"`.
- **Empty states:** charcoal box with a dashed border.

### 8.3 Forms (Search, Filters, Compare)

- Every field has a visible `<label>`.
- Fields are charcoal (`#161618`) with a `#3A3A41` border and white text; the focused field gets a 2px lime outline.
- `color-scheme: dark` makes the browser's own dropdown lists and scrollbars dark as well.
- The forms are plain GET forms, so results can be bookmarked and work without JavaScript.
- Validation messages on the Compare page use a red-tinted panel with `role="alert"`.

### 8.4 Comparison table

- A real `<table>` with a hidden `<caption>`, column headers (`scope="col"`) and row headers (`scope="row"`).
- Header row: `#1D1D20` with a 2px lime line under it.
- Rows alternate between charcoal and `#111113`; labels are grey and values are white and bold.
- Phones: the table keeps its three columns with smaller text and padding. Below 380px wide it scrolls sideways inside its own box rather than squeezing the text.
- Missing values show "Not specified". There are no scores, rankings or winner.

## 9. Responsive Behaviour

| Width | Behaviour |
|---|---|
| **Above 1024px (desktop)** | Sticky header; hero with the illustration on the right; 4-column Explore and type grids; 3-column card grids; two-column page headers; filters and button on one row. |
| **721-1024px (tablet)** | Navigation wraps under the logo; Explore cards in 2 columns; 4-column grids become 2 columns; hero illustration moves further right and fades; Compare promotion stacks; filter buttons move under the dropdowns. |
| **720px and below (phone)** | Single column everywhere; full-width buttons; the hero illustration moves below the buttons; quick links in two columns; page headers stack the image under the text; search, filter and compare fields stack; tables stay readable as in section 8.4. |

The layout was checked with screenshots at 1440px and 390px wide (no sideways scrolling). The tablet range was reviewed in the CSS only.

## 10. Accessibility Features

- High text contrast on every colour combination (section 3).
- A visible **2px lime focus outline** on every link, button and field (`:focus-visible`); it is never removed.
- A **Skip to main content** link; every page's `<main>` has `id="main"`.
- Semantic structure: `header`, `nav` with `aria-label`, `main`, `section`, `footer`, a breadcrumb `nav`, and one `h1` per page.
- The current page in the navigation uses `aria-current="page"`.
- Decorative SVGs and placeholder images use `alt=""` or `aria-hidden="true"`; arrows and numbers are hidden from screen readers.
- Screen-reader-only text completes vague links (for example "Learn more about the Trek 520 Disc").
- Form labels, table headers, a table caption and `role="alert"` for validation messages.
- **Reduced motion:** with "reduce motion" turned on in the operating system, hover lifts, zooms and transitions are switched off.
- Section links stop below the sticky header (`scroll-margin-top: 96px`).

## 11. SVG Illustrations and Image Credits

### 11.1 Images used now

Version 2 contains **no photographs**. The planned stock photos could not be downloaded and checked in the development environment, so the site uses original vector artwork instead.

| File | Used on | Description |
|---|---|---|
| `static/images/bike-hero.svg` | Homepage hero and Anatomy section | Original line illustration of a generic road bicycle with a lime drivetrain and floor glow. No brand marks. |
| `static/images/placeholder-bike.svg` | All image placeholders (types, brands, materials, bicycles, page headers) | Neutral dark bicycle outline with a small lime chainring, marking where a photo will go. |
| Inline SVG in `templates/fragments/layout.html` | Header and footer logo | Original road-bike outline drawn in lime. |
| Inline SVG in `templates/components/overview.html` | Components page | Interactive anatomy diagram (from Version 1, recoloured). |

The SVGs are simple vector drawings written as code for this project. They are not photographs and do not show people, logos or trademarks.

### 11.2 Image credits

All image sources, licence details and rules for adding photographs are recorded in **`IMAGE-CREDITS.md`** in the project root. It also lists six shortlisted Unsplash and Pexels photographs. They are covered by the free licences on their own pages but were **not downloaded or used**, because the images themselves have not yet been checked for logos, people or resolution.

## 12. Differences Between Version 1 and Version 2

| Area | Version 1 | Version 2 |
|---|---|---|
| Overall style | Light, wireframe-based | Dark, cinematic |
| Page background | White `#FFFFFF`, grey sections `#F8F9FB` | Near-black `#0A0A0B`, sections `#111113` |
| Cards | White with light grey border | Charcoal `#161618` with `#26262B` border |
| Text | Dark `#1A1A1A`, muted `#5F6B7A` | White `#F5F5F4`, muted `#A1A1AA` |
| Accent | Blue `#3B7CF4` | Lime `#C8F031` (dark text on lime buttons) |
| Primary button | Dark grey/black with white text | Lime with near-black text |
| Headings | Weight 700, smaller sizes | Weight 800, tighter spacing, much larger hero and page titles |
| Corner radius | 12px | 14px (24px large panels) |
| Header | White, static | Dark, sticky with blur on desktop, lime underline for the current page, skip link |
| Footer | Dark grey `#1C1C1E` | Near-black `#050506`, lime hover, "no shop, no ads, no tracking" note |
| Homepage hero | Two columns: text and a square placeholder | Full-width cinematic hero with original illustration and quick-links bar |
| Explore cards | Small icon cards | Clean, numbered, text-only cards |
| Pros & Cons | Green tick and red cross icons | Plain text lists without symbols |
| Logo | Simple bicycle icon | Original lime road-bike outline with drop handlebar |
| Featured Models | Grey placeholders | Dark model cards with spec line and honest "Photo to be added" label |
| Compare promotion | None on the homepage | New section linking to `/compare` |
| Placeholders | Light grey bicycle outline | Dark outline with small lime accent |
| 404 page | Simple text page | Large outlined "404" with the same messages and links |
| Focus and motion | Browser default focus | Lime focus outline everywhere; reduced-motion support |
| Pages, URLs, data, features | — | Unchanged |

## 13. Version 2 Scope

Version 2 includes:

- the dark design system in `styles.css` (tokens, components, responsive rules)
- the redesigned homepage
- the restyled header, footer and every existing page
- original SVG artwork and the image credits file
- accessibility improvements (skip link, focus outlines, reduced motion)

Version 2 does not change:

- Java code, controllers, services or repositories
- the database schema or bicycle data
- URLs, Search, Filters or Compare behaviour

## 14. Remaining Design Improvements

1. **Real photography.** Review the six shortlisted photos in `IMAGE-CREDITS.md` by viewing each full image. Add only those with no readable logos or identifiable people, then:
   - resize them (about 1600-2400px wide for the hero, about 1000px for cards)
   - store them in `static/images/photos/`
   - record them in `IMAGE-CREDITS.md`
2. **Model photographs.** Keep neutral placeholders on real bicycle model cards until accurate, properly licensed photos of those exact models are available.
3. **Bicycle type images.** Add generic, licensed category photos (road, mountain, gravel, hybrid and others) to replace the placeholders on the Bicycle Types pages.
4. **Stale text on Component Detail.** Materials still show "Material page coming soon" although Material pages now exist; these could become links.
5. **Tablet testing.** Check the 721-1024px range in a real browser or device.
6. **Assistive technology testing.** Test with a screen reader (for example NVDA on Windows) and keyboard-only navigation across all pages.
7. **Optional web font.** A self-hosted, openly licensed display font could strengthen the headings, if it does not slow the site down.
8. **Optional mobile navigation menu.** If more pages are added, the wrapping navigation may need a compact menu.
