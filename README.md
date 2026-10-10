# SpringSecurityConfigTemplate — 5-Decision Model

Stop memorizing Spring Security. Build it decision by decision.

This template is a  `SecurityFilterChain` broken into 5 mental steps.

### The Mental Model

Most tutorials show security as one long pipeline to memorize. This fails because you forget why each line exists.

Instead, think of 5 separate decisions:

| # | Decision | Code | Question to Ask |
|---|---|---|---|
| 1 | What to protect? | `.csrf()` `.cors()` | Are we an API or a browser app? |
| 2 | Session Type? | `.sessionManagement()` | Stateless (JWT) or Stateful (cookies)? |
| 3 | Public vs Private? | `.authorizeHttpRequests()` | Which URLs are open? Order matters! |
| 4 | Custom Filters? | `.addFilterBefore()` | Do we need JWT / API Key check? |
| 5 | How to login? | `.httpBasic()` / `.formLogin()` | How does user prove identity? |

### Quick Start

**For Juniors:**
1. Clone this repo
2. Copy `SpringSecurityConfigTemplate.java` into `com.yourapp.config`
3. Set your public URLs in DECISION 3
4. Done. No other files needed to start.

**Leave these alone if you're just starting:**
- Stateless by default. Change to `ALWAYS` + `formLogin()` for MVC
- CORS is centralized in `corsConfigurationSource()` — add your frontend domains
- Filter order: JWT -> UsernamePasswordFilter. Add RateLimit filter before JWT if needed.
- Replace `httpBasic()` with `oauth2ResourceServer()` when you move to real JWT validation.
