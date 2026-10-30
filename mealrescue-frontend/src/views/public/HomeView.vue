<script setup>
import { computed } from 'vue'
import AppLogo from '@/components/common/AppLogo.vue'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()

// Customers and organizations have no area of their own yet, so the shortcut only shows for roles that do
const areaLink = computed(() =>
  auth.isAuthenticated && auth.homeRoute !== '/' ? auth.homeRoute : null,
)

const steps = [
  {
    title: 'Businesses post surplus food',
    text: 'Bakeries, restaurants and grocers list what is left over, with a quantity, a reduced price and a pickup window.',
  },
  {
    title: 'People reserve what they need',
    text: 'Students and community organizations browse, filter and reserve before it is gone.',
  },
  {
    title: 'Pick up and enjoy',
    text: 'Collect your food during the pickup window, pay at pickup and rate the business.',
  },
]

const audiences = [
  {
    name: 'Businesses',
    text: 'Recover some of the cost of unsold food and show your community you care about waste.',
    tone: 'green',
  },
  {
    name: 'Students and customers',
    text: 'Find good food at a lower price, close to you, with clear pickup times.',
    tone: 'amber',
  },
  {
    name: 'Community organizations',
    text: 'Reserve larger quantities for food banks, shelters and community kitchens in one place.',
    tone: 'dark',
  },
]
</script>

<template>
  <div class="page">
    <header class="top">
      <div class="container bar">
        <router-link to="/" class="brand" aria-label="MealRescue home"><AppLogo /></router-link>
        <nav class="actions" aria-label="Account">
          <template v-if="auth.isAuthenticated">
            <span class="who">{{ auth.user.name }}</span>
            <router-link v-if="areaLink" :to="areaLink" class="btn">Go to my area</router-link>
            <button class="btn btn-light" @click="auth.logout()">Log out</button>
          </template>
          <template v-else>
            <router-link to="/login" class="btn btn-light">Log in</router-link>
            <router-link to="/register" class="btn">Register</router-link>
          </template>
        </nav>
      </div>
    </header>

    <main>
      <section class="hero">
        <div class="container hero-grid">
          <div class="hero-text">
            <p class="eyebrow">Surplus food, shared locally</p>
            <h1>Good food deserves a plate, not a bin.</h1>
            <p class="lead">
              MealRescue connects local bakeries, restaurants and grocery stores that have food left over
              with students and community organizations who can use it. Reserve it at a lower price, pick
              it up before closing time, and keep good food out of the landfill.
            </p>
            <div class="cta">
              <template v-if="!auth.isAuthenticated">
                <router-link to="/register" class="btn big">Create an account</router-link>
                <router-link to="/login" class="btn btn-light big">Log in</router-link>
              </template>
              <router-link v-else-if="areaLink" :to="areaLink" class="btn big">Go to my area</router-link>
            </div>
            <p class="fact">
              About 58% of the food produced in Canada is lost or wasted along the way (Second Harvest, 2019).
            </p>
          </div>

          <!-- Decorative example of what a listing looks like -->
          <div class="sample" aria-hidden="true">
            <span class="tag">Example listing</span>
            <h2>Day-old sourdough loaves</h2>
            <p class="muted">Corner Bakery &middot; Bakery</p>
            <div class="row">
              <span class="price">$2.50</span>
              <span class="left">6 left</span>
            </div>
            <p class="muted">Pickup today, 5:00 to 7:00 PM</p>
            <span class="fake-btn">Reserve</span>
          </div>
        </div>
      </section>

      <section class="section">
        <div class="container">
          <h2>How it works</h2>
          <ol class="steps">
            <li v-for="(s, i) in steps" :key="s.title">
              <span class="num">{{ i + 1 }}</span>
              <h3>{{ s.title }}</h3>
              <p>{{ s.text }}</p>
            </li>
          </ol>
        </div>
      </section>

      <section class="section alt">
        <div class="container">
          <h2>Who it is for</h2>
          <div class="cards">
            <article v-for="a in audiences" :key="a.name" class="card" :class="a.tone">
              <h3>{{ a.name }}</h3>
              <p>{{ a.text }}</p>
            </article>
          </div>
        </div>
      </section>

      <section v-if="!auth.isAuthenticated" class="band">
        <div class="container band-inner">
          <h2>Ready to rescue some food?</h2>
          <div class="cta">
            <router-link to="/register" class="btn big">Register</router-link>
            <router-link to="/login" class="btn btn-light big">Log in</router-link>
          </div>
        </div>
      </section>
    </main>

    <footer class="foot">
      <div class="container">&copy; 2026 Meal Rescue</div>
    </footer>
  </div>
</template>

<style scoped>
.page {
  background: #f6f7f4;
  color: #1d2521;
  min-height: 100vh;
}
.container {
  max-width: 1100px;
  margin: 0 auto;
  padding: 0 1rem;
}

/* ---------- top bar ---------- */
.top {
  background: #fff;
  border-bottom: 1px solid #d9dfda;
}
.bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 0.5rem 1rem;
  min-height: 68px;
}
.brand {
  text-decoration: none;
}
.actions {
  display: flex;
  align-items: center;
  gap: 0.6rem;
}
.actions .btn {
  white-space: nowrap;
}
.who {
  color: #5d6a63;
  margin-right: 0.25rem;
}

/* ---------- hero ---------- */
.hero {
  background: linear-gradient(180deg, #e6efe8 0%, #f6f7f4 100%);
  padding: 3.5rem 0 4rem;
}
.hero-grid {
  display: grid;
  grid-template-columns: 1.15fr 0.85fr;
  gap: 3rem;
  align-items: center;
}
.eyebrow {
  margin: 0 0 0.75rem;
  color: #9a6a0f;
  font-weight: 700;
  font-size: 0.85rem;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}
h1 {
  margin: 0 0 1rem;
  font-family: Georgia, 'Times New Roman', serif;
  font-size: clamp(2.2rem, 5vw, 3.4rem);
  line-height: 1.1;
  color: #1d2521;
}
.lead {
  margin: 0 0 1.5rem;
  font-size: 1.1rem;
  line-height: 1.6;
  color: #36423b;
  max-width: 34rem;
}
.cta {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
}
.big {
  padding: 0.8rem 1.5rem;
  font-size: 1.05rem;
}
.fact {
  margin: 1.5rem 0 0;
  font-size: 0.9rem;
  color: #5d6a63;
  max-width: 30rem;
}

/* example listing card */
.sample {
  position: relative;
  background: #fff;
  border: 1px solid #d9dfda;
  border-radius: 16px;
  padding: 1.5rem;
  box-shadow: 0 18px 40px -18px rgba(33, 76, 56, 0.35);
  transform: rotate(-1.5deg);
}
.sample h2 {
  margin: 0.75rem 0 0.25rem;
  font-size: 1.35rem;
}
.sample p {
  margin: 0.25rem 0;
}
.muted {
  color: #5d6a63;
}
.tag {
  display: inline-block;
  padding: 0.15rem 0.6rem;
  border-radius: 999px;
  background: #e4f1ea;
  color: #1f5c3f;
  font-size: 0.8rem;
  font-weight: 600;
}
.row {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin: 0.75rem 0 0.5rem;
}
.price {
  padding: 0.15rem 0.7rem;
  border-radius: 8px;
  background: #fbf0d6;
  color: #7a5200;
  font-weight: 700;
  font-size: 1.2rem;
}
.left {
  color: #1f5c3f;
  font-weight: 600;
}
.fake-btn {
  display: inline-block;
  margin-top: 0.75rem;
  padding: 0.5rem 1.2rem;
  border-radius: 8px;
  background: #2f6b4f;
  color: #fff;
  font-weight: 600;
}

/* ---------- sections ---------- */
.section {
  padding: 3.5rem 0;
}
.section.alt {
  background: #fff;
  border-top: 1px solid #d9dfda;
  border-bottom: 1px solid #d9dfda;
}
.section h2 {
  margin: 0 0 1.75rem;
  font-family: Georgia, 'Times New Roman', serif;
  font-size: 1.9rem;
}
.steps {
  list-style: none;
  margin: 0;
  padding: 0;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1.5rem;
}
.steps h3 {
  margin: 0.75rem 0 0.35rem;
}
.steps p {
  margin: 0;
  color: #36423b;
  line-height: 1.55;
}
.num {
  display: inline-grid;
  place-items: center;
  width: 2.2rem;
  height: 2.2rem;
  border-radius: 50%;
  background: #2f6b4f;
  color: #fff;
  font-weight: 700;
}
.cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1.5rem;
}
.card {
  background: #f6f7f4;
  border: 1px solid #d9dfda;
  border-top-width: 5px;
  border-radius: 10px;
  padding: 1.25rem;
}
.card.green { border-top-color: #2f6b4f; }
.card.amber { border-top-color: #c98a1b; }
.card.dark { border-top-color: #1d2521; }
.card h3 {
  margin: 0 0 0.5rem;
}
.card p {
  margin: 0;
  line-height: 1.55;
  color: #36423b;
}

/* ---------- call to action band ---------- */
.band {
  background: #2f6b4f;
  color: #fff;
  padding: 2.5rem 0;
}
.band-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1.5rem;
  flex-wrap: wrap;
}
.band h2 {
  margin: 0;
  font-family: Georgia, 'Times New Roman', serif;
  font-size: 1.7rem;
}
.band .btn:not(.btn-light) {
  background: #e0a43a;
  color: #1d2521;
}

.foot {
  padding: 1.5rem 0;
  color: #5d6a63;
  font-size: 0.9rem;
}

/* ---------- small screens ---------- */
@media (max-width: 820px) {
  .hero-grid,
  .steps,
  .cards {
    grid-template-columns: 1fr;
  }
  .hero {
    padding: 2.25rem 0 2.75rem;
  }
  .sample {
    transform: none;
  }
  .actions {
    gap: 0.4rem;
  }
  .actions .btn {
    padding: 0.4rem 0.7rem;
    font-size: 0.9rem;
  }
}
</style>