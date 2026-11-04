<script setup>
// Placeholder details for the course project, not real contact information
const CONTACT = {
  email: 'support@mealrescue.example',
  phone: '604-555-0123',
  phoneLink: '+16045550123',
  address: ['123 Harvest Lane', 'Surrey, BC V3T 0A1', 'Canada'],
}
</script>

<template>
  <section class="contact">
    <h1>Contact us</h1>
    <p class="intro">
      Questions about reserving food, listing your surplus, or getting your organization verified?
      Get in touch and we'll reply within two business days.
    </p>

    <dl>
      <div>
        <dt>Email</dt>
        <dd><a :href="`mailto:${CONTACT.email}`">{{ CONTACT.email }}</a></dd>
      </div>
      <div>
        <dt>Phone</dt>
        <dd><a :href="`tel:${CONTACT.phoneLink}`">{{ CONTACT.phone }}</a></dd>
        <dd class="muted">Monday to Friday, 9:00 to 17:00</dd>
      </div>
      <div>
        <dt>Address</dt>
        <dd>
          <span v-for="line in CONTACT.address" :key="line" class="line">{{ line }}</span>
        </dd>
      </div>
    </dl>
  </section>
</template>

<style scoped>
.contact {
  background: #fff;
  border: 1px solid #d9dfda;
  border-radius: 10px;
  padding: 1.25rem 1.5rem;
  max-width: 560px;
}
h1 {
  margin-top: 0;
}
.intro {
  color: #5d6a63;
  line-height: 1.6;
}
dl {
  display: grid;
  gap: 1.1rem;
  margin: 1.5rem 0 0;
}
dt {
  color: #5d6a63;
  font-size: 0.85rem;
}
dd {
  margin: 0.15rem 0 0;
  font-weight: 600;
}
dd a {
  color: #2f6b4f;
}
.muted {
  color: #5d6a63;
  font-weight: 400;
  font-size: 0.9rem;
}
.line {
  display: block;
}
</style>