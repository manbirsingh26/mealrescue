# MealRescue

MealRescue is a web platform that connects local businesses that have surplus food (bakeries, restaurants, grocery stores) with customers and community organizations who can use it. Businesses post what is left over at a reduced price with a pickup window, people reserve it online, and the business confirms the pickup with a one-time code. The goal is to keep good food out of the landfill.

The project was built for **CSIS 3175 Software Engineering** at Douglas College (Fall 2026).

- **Team:** Manbir and Mani Singh
- **Instructor:** Zhe Yang

---

## Contents

1. [Features](#features)
2. [Technology](#technology)
3. [Project structure](#project-structure)
4. [Getting started](#getting-started)
5. [How the main rules work](#how-the-main-rules-work)
6. [REST API overview](#rest-api-overview)
7. [User stories](#user-stories)
8. [Known limitations](#known-limitations)

---

## Features

### Everyone (no account needed)
- Landing page explaining the platform, with a live **"Rescued so far"** impact section (portions rescued, estimated food weight and CO₂ avoided, participating businesses).
- **Current listings:** browse active listings, search by title or business name, and filter by category, maximum price and pickup time. Sort by ending soonest, lowest price or newest.
- Listing details with the description, remaining quantity, pickup window, pickup address and the business's reviews.
- Contact page.

### Customers and organizations
- Register and log in (organizations must be approved by an admin first).
- **Reserve** food with a per-listing limit: up to **5** portions per customer and **50** per organization on each listing.
- Receive a **6-digit pickup code** for every reservation.
- **Your reservations:** upcoming pickups with their codes and pickup location, and past reservations. Cancel an upcoming reservation, which releases the food for others.
- **Review** a business (1 to 5 stars and an optional comment) after a completed pickup.
- **Report** a listing or a business, and follow each report's status and the admin's reply on the profile page.

### Businesses
- Register with business details (must be approved by an admin first).
- Create, edit and cancel listings (title, description, category, quantity, price, pickup window).
- **Reservations:** see who reserved what, filter by status, and **confirm pickups** by entering the customer's code.
- See the reviews customers have left.

### Admins
- **Dashboard:** platform statistics (users, listings, reservations, portions rescued, value recovered, pickup rate, average rating, top businesses and a 14-day chart).
- **Verification queue:** approve or reject new businesses and organizations (rejections record a reason).
- **Users:** search and filter all accounts, deactivate or reactivate them.
- **Listings:** search and filter all listings, remove inappropriate ones.
- **Reports:** review reports, take action (remove the listing or deactivate the business), then resolve or dismiss them with a note to the reporter.

### All logged-in users
- **Profile page:** edit name, email, address and business or organization type. Changes show immediately.
- **Secure logout:** logging out invalidates the token on the server, which ends the session on every device.

---

## Technology

| Layer | Technology |
|---|---|
| Back end | Java 21, Spring Boot 4 (Web, Security, Data JPA, Validation), Maven |
| Authentication | Stateless JWT (jjwt), BCrypt password hashing |
| Database | Microsoft SQL Server, Hibernate ORM |
| Front end | Vue 3 (`<script setup>`), Vite, Vue Router, Pinia, Axios |
| Styling | Plain CSS with scoped component styles |

The back end is a RESTful web service. The front end is a single-page application that talks to it through `/api`, which Vite proxies to the back end during development.

---

## Project structure

```
mealrescue/
├── mealrescue-backend/                  Spring Boot REST API
│   ├── src/main/java/com/mealrescue/
│   │   ├── config/        Security configuration, admin seeder, scheduled expiry job
│   │   ├── controller/    REST controllers
│   │   ├── dto/           Request and response records
│   │   ├── exception/     ApiException and the global error handler
│   │   ├── model/         JPA entities and enums
│   │   ├── repository/    Spring Data repositories
│   │   ├── security/      JWT service and authentication filter
│   │   └── service/       Business logic
│   └── src/main/resources/
│       ├── application.properties
│       └── application-local.properties   (not in Git, see below)
│
└── mealrescue-frontend/                 Vue 3 single-page app
    └── src/
        ├── api/           Axios instance and API calls per feature
        ├── assets/        Global styles and images
        ├── components/    Shared components (navbar, footer, badges, dialogs, listing cards, reviews, reports)
        ├── layouts/       DefaultLayout (navbar + footer) and AuthLayout (login/register)
        ├── router/        Routes and the role-based navigation guard
        ├── stores/        Pinia auth store
        ├── utils/         Constants, formatters, validators
        └── views/         Pages, grouped by public, auth, customer, business, admin and profile
```

---

## Getting started

### Prerequisites

- **JDK 21 or newer**
- **Node.js 22 LTS** (or another version supported by the Vite version in `package.json`)
- **Microsoft SQL Server** (Developer or Express edition) and **SQL Server Management Studio**
- Git

Maven does not need to be installed: the project includes the Maven wrapper (`mvnw.cmd`).

### 1. Set up the database

1. In **SQL Server Configuration Manager**, enable **TCP/IP** for your SQL Server instance and make sure it listens on port **1433**. Restart the SQL Server service afterwards.
2. Make sure **SQL Server and Windows Authentication mode** is enabled (server properties → Security in SSMS).
3. In SSMS, create the database and a login for the application:

```sql
CREATE DATABASE mealrescue;
GO
CREATE LOGIN mealrescue_user WITH PASSWORD = 'ChooseAStrongPassword1!';
GO
USE mealrescue;
CREATE USER mealrescue_user FOR LOGIN mealrescue_user;
ALTER ROLE db_owner ADD MEMBER mealrescue_user;
GO
```

You don't need to create any tables. Hibernate creates and updates them when the back end starts (`spring.jpa.hibernate.ddl-auto=update`).

### 2. Configure the back end

Secrets are kept out of Git. Create the file
`mealrescue-backend/src/main/resources/application-local.properties` with your own values:

```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=mealrescue;encrypt=true;trustServerCertificate=true
spring.datasource.username=mealrescue_user
spring.datasource.password=ChooseAStrongPassword1!

# At least 32 characters; used to sign login tokens
app.jwt.secret=replace-with-a-long-random-secret-of-32-plus-characters

# The admin account is created automatically on first start if it does not exist
app.admin.email=admin@mealrescue.local
app.admin.password=ChooseAnAdminPassword1!
```

`application.properties` already activates this `local` profile.

### 3. Run the back end

```cmd
cd mealrescue-backend
mvnw.cmd spring-boot:run
```

The API runs on **http://localhost:8080**. On the first start it creates the tables and the admin account.

### 4. Run the front end

In a second terminal:

```cmd
cd mealrescue-frontend
npm install
npm run dev
```

Open **http://localhost:5173**. Calls to `/api` are forwarded to the back end automatically.

### 5. Demo data

On its first start with an **empty** database, the back end fills it with realistic sample data (`app.demo-data=true` in `application.properties`). You can log in straight away as any role:

| Role | Email | Password |
|---|---|---|
| Admin | the `app.admin.email` from `application-local.properties` | the `app.admin.password` you chose |
| Business (bakery) | `bakery@example.com` | `Demo1234!` |
| Business (café) | `cafe@example.com` | `Demo1234!` |
| Business (grocer) | `grocer@example.com` | `Demo1234!` |
| Organization | `pantry@example.com` | `Demo1234!` |
| Customer | `priya@example.com` | `Demo1234!` |
| Customer | `jordan@example.com` | `Demo1234!` |
| Customer | `emma@example.com` | `Demo1234!` |

Two more accounts, `deli@example.com` (business) and `shelter@example.com` (organization), are **pending**. They appear in the admin's Verification queue and can't log in until approved.

The data includes current listings in every category (open now and for the next few days), sold-out, cancelled, removed and past listings, reservations in every status over the last two weeks, reviews, and reports (open, resolved and dismissed). All names, emails and addresses are invented.

Good to know:

- The seeder runs only when the database has no business accounts, so restarting never creates duplicates and it never touches existing data.
- Pickup times are set relative to the moment the data is created. After a few days, current listings expire like real ones. To get fresh demo data, drop and recreate the database, then start the back end again.
- To start with an empty database instead, set `app.demo-data=false` in `application-local.properties`.

### 6. Try it out

1. Log in as `bakery@example.com` and open **Reservations**: customers are waiting to pick up.
2. Log in as `priya@example.com`, open **Your reservations**, and note the pickup code of an upcoming reservation.
3. Back as the bakery, enter that code under **Confirm a pickup**.
4. Log in as the admin to see the **Dashboard**, the **Verification queue** and the open **Reports**.

---

## How the main rules work

**Roles and access.** Every API route is protected by role in `SecurityConfig`, and every page by the router guard. A user who tries a page for another role sees a "Forbidden" page. A business that requests another business's listing or reservation gets "not found".

**Account approval.** Businesses and organizations start as **Pending** and can't log in until an admin approves them. Customers are active straight away. An admin can deactivate any non-admin account later. A deactivated user is logged out on their next request.

**Login tokens.** Logins return a signed JWT that is valid for 120 minutes. Each user has a **token version**. Logging out increases it, which makes every older token for that user invalid immediately.

**Listings.** A listing is **Active** until it sells out (**Sold out**), its pickup window ends (**Expired**), the business cancels it (**Cancelled**) or an admin removes it (**Removed**). Listings are never deleted, so reservation history, reviews and statistics stay intact.

**Reservations.**
- Reserving reduces the remaining quantity in one database transaction. The `Listing` entity uses **optimistic locking** (`@Version`), so two people can never both reserve the last portion. One of them gets a "please try again" error instead.
- Each customer can hold at most **5** portions of a listing, and each organization **50**. Cancelled and expired reservations don't count toward the limit.
- A reservation is **Reserved** until it is **Picked up** (confirmed by the business with the code), **Cancelled** (by the customer, or automatically when the listing is cancelled or removed), or **Expired** (not collected before the pickup window ended).
- The price is saved on the reservation when it is made.

**Automatic expiry.** A scheduled job runs every minute and marks ended listings and uncollected reservations as **Expired**.

**Pickup codes.** Each reservation gets a random 6-digit code that is unique among active reservations. The business sees who reserved but never the code, so the customer has to show it in person.

**Reviews and reports.** Only reservations marked **Picked up** can be reviewed, once each. A user can have only one open report about the same listing or business at a time.

**Impact estimates.** The homepage impact section counts only picked-up reservations. Food weight and CO₂ are estimates, based on about 0.4 kg of food per portion and 2.5 kg of CO₂e avoided per kg of food not wasted. Both numbers are constants in `ImpactService`.

---

## REST API overview

All endpoints are under `/api`. Errors are returned as `{ "message": "..." }` with a matching HTTP status (400 invalid input, 401 not logged in, 403 not allowed, 404 not found, 409 conflict).

| Area | Method and path | Who |
|---|---|---|
| Auth | `POST /auth/register`, `POST /auth/login` | Anyone |
| Account | `GET /users/me`, `GET`/`PUT /users/me/profile`, `POST /users/me/logout` | Logged in |
| Listings (public) | `GET /listings` (search and filters), `GET /listings/{id}` | Anyone |
| Listings (business) | `POST /listings`, `PUT /listings/{id}`, `POST /listings/{id}/cancel`, `GET /business/listings`, `GET /business/listings/{id}` | Business |
| Reservations | `POST /reservations`, `GET /reservations`, `POST /reservations/{id}/cancel` | Customer, organization |
| Business reservations | `GET /business/reservations`, `POST /business/reservations/confirm` | Business |
| Reviews | `POST /reservations/{id}/review` (customer, organization), `GET /businesses/{id}/reviews` (anyone) | See left |
| Reports | `POST /reports`, `GET /reports` | Customer, organization |
| Impact | `GET /impact` | Anyone |
| Admin | `GET /admin/stats`, `GET /admin/pending-accounts`, `POST /admin/accounts/{id}/approve`, `POST /admin/accounts/{id}/reject`, `GET /admin/users`, `POST /admin/users/{id}/deactivate`, `POST /admin/users/{id}/reactivate`, `GET /admin/listings`, `POST /admin/listings/{id}/remove`, `GET /admin/reports`, `POST /admin/reports/{id}/resolve`, `POST /admin/reports/{id}/dismiss` | Admin |

---

## User stories

| ID | Role | Story | Status |
|---|---|---|---|
| US-01 | Customer | Register and log in | Done |
| US-02 | Business | Register with my business details | Done |
| US-03 | Organization | Register as a community organization | Done |
| US-04 | Registered user | Log out and edit my profile | Done |
| US-05 | Business | Create a surplus-food listing | Done |
| US-06 | Business | Edit or cancel my listings | Done |
| US-07 | Business | View the reservations for my listings | Done |
| US-08 | Business | Confirm a pickup | Done |
| US-09 | Business | Expired listings and reservations close automatically | Done |
| US-10 | Customer | Browse and search listings | Done |
| US-11 | Customer | Filter listings by category, price and pickup time | Done |
| US-12 | Customer | View listing details | Done |
| US-13 | Customer | Reserve a quantity of a listing | Done |
| US-14 | Customer | Cancel a reservation | Done |
| US-15 | Customer | View my reservation history | Done |
| US-16 | Customer | Rate and review a business after pickup | Done |
| US-17 | Customer | Report a listing or business | Done |
| US-18 | Organization | Reserve larger quantities than regular customers | Done |
| US-19 | Organization | Manage my upcoming and past pickups | Done |
| US-20 | Admin | Approve or reject businesses and organizations | Done |
| US-21 | Admin | Manage users and listings | Done |
| US-22 | Admin | Review and resolve reports | Done |
| US-23 | Admin | View platform statistics | Done |
| US-24 | Business | Price drops automatically as the pickup deadline approaches | Not implemented (stretch, deprioritized) |
| US-25 | Customer | See my personal impact | Changed: combined with US-26 into a public impact section on the homepage |
| US-26 | Business | See how much food my business has rescued | Changed: combined with US-25 into a public impact section on the homepage |

---

## Known limitations

- Payment happens in person at pickup; the platform does not process payments.
- Times are stored in the server's local time zone, which is fine for a single-city deployment.
- Food weight and CO₂ figures are estimates, not measurements.
- There is no email sending (for example password reset or notifications).
