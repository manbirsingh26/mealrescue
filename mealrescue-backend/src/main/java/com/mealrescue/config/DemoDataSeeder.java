package com.mealrescue.config;

import com.mealrescue.model.*;
import com.mealrescue.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Fills an EMPTY database with realistic sample data so the app can be evaluated straight away.
 * Runs only when app.demo-data=true and only if no business account exists yet.
 * All names, emails and addresses are invented; every account's password is DEMO_PASSWORD.
 */
@Component
@Order(2) // after AdminSeeder
@ConditionalOnProperty(name = "app.demo-data", havingValue = "true")
public class DemoDataSeeder implements CommandLineRunner {

    public static final String DEMO_PASSWORD = "Demo1234!";

    private static final Logger log = LoggerFactory.getLogger(DemoDataSeeder.class);

    private final UserRepository users;
    private final ListingRepository listings;
    private final ReservationRepository reservations;
    private final ReviewRepository reviews;
    private final ReportRepository reports;
    private final PasswordEncoder passwordEncoder;

    private LocalDateTime now;
    private int codeCounter = 0;
    private int listingCount = 0;
    private int reservationCount = 0;

    public DemoDataSeeder(UserRepository users, ListingRepository listings,
                          ReservationRepository reservations, ReviewRepository reviews,
                          ReportRepository reports, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.listings = listings;
        this.reservations = reservations;
        this.reviews = reviews;
        this.reports = reports;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (users.existsByRole(Role.BUSINESS)) {
            log.info("Demo data skipped: the database already has data.");
            return;
        }
        now = LocalDateTime.now().withSecond(0).withNano(0);
        String hash = passwordEncoder.encode(DEMO_PASSWORD);

        // ---------- accounts ----------
        User bakery = user("Harvest Moon Bakery", "bakery@example.com", Role.BUSINESS, AccountStatus.ACTIVE, hash,
                "12 Orchard Way, Surrey, BC", "Bakery");
        User cafe = user("Green Fork Café", "cafe@example.com", Role.BUSINESS, AccountStatus.ACTIVE, hash,
                "88 Maple Crescent, Surrey, BC", "Café");
        User grocer = user("Fraser Fresh Grocer", "grocer@example.com", Role.BUSINESS, AccountStatus.ACTIVE, hash,
                "301 Riverbend Road, Surrey, BC", "Grocery store");
        User pantry = user("Surrey Community Pantry", "pantry@example.com", Role.ORGANIZATION, AccountStatus.ACTIVE, hash,
                "45 Willow Street, Surrey, BC", "Food bank");
        User priya = user("Priya Sharma", "priya@example.com", Role.CUSTOMER, AccountStatus.ACTIVE, hash, null, null);
        User jordan = user("Jordan Lee", "jordan@example.com", Role.CUSTOMER, AccountStatus.ACTIVE, hash, null, null);
        User emma = user("Emma Wilson", "emma@example.com", Role.CUSTOMER, AccountStatus.ACTIVE, hash, null, null);
        user("Riverside Deli", "deli@example.com", Role.BUSINESS, AccountStatus.PENDING, hash,
                "9 Harbour Lane, Surrey, BC", "Deli");
        user("Hope Shelter Kitchen", "shelter@example.com", Role.ORGANIZATION, AccountStatus.PENDING, hash,
                "210 Elm Avenue, Surrey, BC", "Shelter");

        // ---------- current listings (open now or soon, for a few days) ----------
        Listing sourdough = listing(bakery, "Day-old sourdough loaves",
                "Crusty sourdough baked yesterday. Great for toast and sandwiches.",
                FoodCategory.BAKERY, 12, "2.50", now.minusHours(1), now.plusDays(3));
        listing(bakery, "Assorted pastries box",
                "Six mixed pastries: croissants, danishes and muffins.",
                FoodCategory.BAKERY, 8, "5.00", now.plusHours(2), now.plusDays(4));
        Listing bagels = listing(bakery, "Bagels (bag of 6)",
                "Plain and sesame bagels from this morning.",
                FoodCategory.BAKERY, 4, "3.00", now.minusHours(1), now.plusDays(2));
        Listing curry = listing(cafe, "Vegetable curry with rice",
                "Freshly made this afternoon. Bring your own container if you can.",
                FoodCategory.PREPARED_MEALS, 15, "4.00", now.minusHours(2), now.plusDays(2));
        listing(cafe, "Garden salad bowls",
                "Mixed greens with chickpeas and house dressing.",
                FoodCategory.PREPARED_MEALS, 6, "3.50", now.plusHours(1), now.plusDays(2));
        Listing soup = listing(cafe, "Lentil soup (1 L)",
                "Hearty red lentil soup, ready to reheat.",
                FoodCategory.PREPARED_MEALS, 10, "0.00", now.minusHours(1), now.plusDays(3));
        listing(grocer, "Ripe bananas (bunch)",
                "Spotty but sweet. Perfect for baking or smoothies.",
                FoodCategory.PRODUCE, 20, "0.00", now.minusHours(3), now.plusDays(2));
        Listing yogurt = listing(grocer, "Greek yogurt tubs",
                "Plain Greek yogurt, best before in two days.",
                FoodCategory.DAIRY, 18, "1.50", now.minusHours(1), now.plusDays(2));
        Listing groceryBox = listing(grocer, "Mixed grocery box",
                "Pasta, canned beans, rice and tomato sauce. Feeds a family for a few meals.",
                FoodCategory.GROCERIES, 30, "6.00", now.minusHours(2), now.plusDays(5));
        listing(grocer, "Orange juice (1 L)",
                "Close to its best-before date, still sealed.",
                FoodCategory.BEVERAGES, 10, "1.00", now.plusHours(3), now.plusDays(3));

        Listing pastaBake = listing(cafe, "Pasta bake trays",
                "Family-size trays of baked penne.",
                FoodCategory.PREPARED_MEALS, 5, "4.50", now.plusHours(1), now.plusDays(1));
        Listing snackBags = listing(grocer, "Mystery snack bags",
                "Assorted chips, bars and crackers.",
                FoodCategory.OTHER, 10, "2.00", now.minusHours(1), now.plusDays(2));

        // ---------- current reservations ----------
        reserved(sourdough, priya, 2);
        reserved(curry, jordan, 1);
        cancelled(curry, emma, 2);
        reserved(groceryBox, pantry, 20);
        reserved(yogurt, emma, 3);
        reserved(soup, jordan, 1);
        reserved(bagels, priya, 2);
        reserved(bagels, jordan, 2);
        bagels.setStatus(ListingStatus.SOLD_OUT); // all 4 reserved

        cancelled(pastaBake, emma, 2);
        pastaBake.setStatus(ListingStatus.CANCELLED); // business cancelled it
        cancelled(snackBags, jordan, 1);
        snackBags.setStatus(ListingStatus.REMOVED);   // admin took it down after a report

        // ---------- the last two weeks: picked up, missed and cancelled ----------
        Listing rolls = past(bakery, "Cinnamon rolls", FoodCategory.BAKERY, 10, "2.00", 13);
        review(pickedUp(rolls, priya, 2, 20), 5, "Still soft and so good. Thank you!");
        pickedUp(rolls, pantry, 6, 45);
        missed(rolls, jordan, 1);
        finish(rolls);

        Listing wraps = past(cafe, "Chicken wraps", FoodCategory.PREPARED_MEALS, 8, "3.50", 11);
        review(pickedUp(wraps, jordan, 2, 15), 4, "Tasty and filling, just a little cold.");
        pickedUp(wraps, emma, 1, 40);
        finish(wraps);

        Listing apples = past(grocer, "Apples (2 lb bag)", FoodCategory.PRODUCE, 15, "1.00", 10);
        review(pickedUp(apples, pantry, 10, 30), 5, "A big help for our Friday hampers.");
        pickedUp(apples, priya, 2, 50);
        pickedUp(apples, emma, 3, 70);
        finish(apples);

        Listing baguettes = past(bakery, "Baguettes", FoodCategory.BAKERY, 10, "1.50", 8);
        review(pickedUp(baguettes, emma, 2, 10), 5, "Crusty and fresh. Great value.");
        missed(baguettes, jordan, 2);
        finish(baguettes);

        Listing butterChicken = past(cafe, "Butter chicken with naan", FoodCategory.PREPARED_MEALS, 12, "4.50", 6);
        review(pickedUp(butterChicken, priya, 1, 25), 4, "Generous portion, friendly staff.");
        pickedUp(butterChicken, pantry, 8, 40);
        finish(butterChicken);

        Listing milk = past(grocer, "Milk (2 L)", FoodCategory.DAIRY, 12, "1.25", 5);
        pickedUp(milk, jordan, 2, 20);
        review(pickedUp(milk, emma, 2, 35), 3, "Fine, but the pickup line was long.");
        finish(milk);

        Listing muffins = past(bakery, "Muffins (4 pack)", FoodCategory.BAKERY, 9, "2.50", 3);
        pickedUp(muffins, priya, 2, 15);
        review(pickedUp(muffins, jordan, 2, 30), 5, "Blueberry ones were amazing.");
        cancelled(muffins, emma, 1);
        finish(muffins);

        Listing bundle = past(grocer, "Bread and soup bundle", FoodCategory.GROCERIES, 10, "3.00", 2);
        pickedUp(bundle, pantry, 6, 20);
        missed(bundle, emma, 1);
        finish(bundle);

        Listing lasagna = past(cafe, "Veggie lasagna", FoodCategory.PREPARED_MEALS, 6, "4.00", 1);
        pickedUp(lasagna, jordan, 2, 30);
        review(pickedUp(lasagna, priya, 2, 45), 5, "Best lasagna I've had in a while.");
        finish(lasagna);

        // ---------- reports ----------
        report(emma, curry, cafe, ReportReason.WRONG_INFORMATION,
                "The listing says vegetable curry, but the label on the container listed chicken stock.",
                now.minusHours(3));
        report(jordan, null, grocer, ReportReason.NOT_AVAILABLE_AT_PICKUP,
                "I arrived during the pickup window and the staff couldn't find my reservation.",
                now.minusDays(1));
        handled(report(priya, snackBags, grocer, ReportReason.UNSAFE_FOOD,
                        "Several bags were weeks past their best-before date.",
                        now.minusHours(20)),
                ReportStatus.RESOLVED, "Thank you. We removed this listing and contacted the store.");
        handled(report(jordan, null, cafe, ReportReason.RUDE_OR_INAPPROPRIATE,
                        "Staff seemed annoyed when I came to pick up.",
                        now.minusDays(4)),
                ReportStatus.DISMISSED, "We looked into this and found no further issues. Thanks for letting us know.");

        log.info("Demo data created: {} listings and {} reservations. Demo password: {}",
                listingCount, reservationCount, DEMO_PASSWORD);
    }

    // ---------- helpers ----------

    private User user(String name, String email, Role role, AccountStatus status, String hash,
                      String address, String type) {
        User u = new User();
        u.setName(name);
        u.setEmail(email);
        u.setRole(role);
        u.setStatus(status);
        u.setPasswordHash(hash);
        if (address != null || type != null) {
            AccountProfile p = new AccountProfile();
            p.setUser(u);
            p.setAddress(address);
            p.setBusinessType(type);
            u.setProfile(p);
        }
        return users.save(u);
    }

    private Listing listing(User business, String title, String description, FoodCategory category,
                            int quantity, String price, LocalDateTime start, LocalDateTime end) {
        Listing l = new Listing();
        l.setBusiness(business);
        l.setTitle(title);
        l.setDescription(description);
        l.setCategory(category);
        l.setQuantityTotal(quantity);
        l.setQuantityRemaining(quantity);
        l.setPrice(new BigDecimal(price));
        l.setPickupStart(start);
        l.setPickupEnd(end);
        l.setStatus(ListingStatus.ACTIVE);
        LocalDateTime posted = start.minusHours(4);
        l.setCreatedAt(posted.isBefore(now) ? posted : now);
        listingCount++;
        return listings.save(l);
    }

    // a listing from earlier in the two weeks: pickup 17:00 to 19:30 that day
    private Listing past(User business, String title, FoodCategory category, int quantity, String price, int daysAgo) {
        LocalDateTime start = LocalDate.now().minusDays(daysAgo).atTime(17, 0);
        return listing(business, title, "Surplus from the day's production.", category, quantity, price,
                start, start.plusMinutes(150));
    }

    // a finished listing ends as sold out or expired, like the expiry job would leave it
    private void finish(Listing l) {
        l.setStatus(l.getQuantityRemaining() == 0 ? ListingStatus.SOLD_OUT : ListingStatus.EXPIRED);
    }

    private Reservation reservation(Listing listing, User customer, int quantity,
                                    ReservationStatus status, LocalDateTime createdAt) {
        Reservation r = new Reservation();
        r.setListing(listing);
        r.setCustomer(customer);
        r.setQuantity(quantity);
        r.setTotalPrice(listing.getPrice().multiply(BigDecimal.valueOf(quantity)));
        r.setStatus(status);
        r.setPickupCode(String.format("%06d", 100000 + (codeCounter++ * 7919) % 900000));
        r.setCreatedAt(createdAt);
        // cancelled reservations gave their portions back; every other status keeps them
        if (status != ReservationStatus.CANCELLED) {
            listing.setQuantityRemaining(listing.getQuantityRemaining() - quantity);
        }
        reservationCount++;
        return reservations.save(r);
    }

    private Reservation reserved(Listing l, User customer, int quantity) {
        return reservation(l, customer, quantity, ReservationStatus.RESERVED, now.minusMinutes(40));
    }

    private Reservation cancelled(Listing l, User customer, int quantity) {
        LocalDateTime made = l.getPickupStart().isBefore(now) ? l.getPickupStart().minusHours(2) : now.minusHours(2);
        return reservation(l, customer, quantity, ReservationStatus.CANCELLED, made);
    }

    private Reservation pickedUp(Listing l, User customer, int quantity, int minutesAfterStart) {
        Reservation r = reservation(l, customer, quantity, ReservationStatus.PICKED_UP,
                l.getPickupStart().minusHours(2));
        r.setPickedUpAt(l.getPickupStart().plusMinutes(minutesAfterStart));
        return r;
    }

    private Reservation missed(Listing l, User customer, int quantity) {
        return reservation(l, customer, quantity, ReservationStatus.EXPIRED, l.getPickupStart().minusHours(1));
    }

    private void review(Reservation r, int rating, String comment) {
        Review v = new Review();
        v.setReservation(r);
        v.setCustomer(r.getCustomer());
        v.setBusiness(r.getListing().getBusiness());
        v.setRating(rating);
        v.setComment(comment);
        v.setCreatedAt(r.getPickedUpAt().plusHours(3));
        reviews.save(v);
    }

    private Report report(User reporter, Listing listing, User business, ReportReason reason,
                          String details, LocalDateTime createdAt) {
        Report r = new Report();
        r.setReporter(reporter);
        r.setListing(listing);
        r.setBusiness(business);
        r.setReason(reason);
        r.setDetails(details);
        r.setCreatedAt(createdAt);
        return reports.save(r);
    }

    private void handled(Report r, ReportStatus outcome, String note) {
        r.setStatus(outcome);
        r.setAdminNote(note);
        r.setHandledAt(r.getCreatedAt().plusHours(5));
    }
}