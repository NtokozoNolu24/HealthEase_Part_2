package com.example.healthease.data.seed

import android.content.Context
import com.example.healthease.data.local.ArticleDao
import com.example.healthease.data.local.ArticleEntity

object ArticleSeeder {

    suspend fun seedIfEmpty(context: Context, dao: ArticleDao) {
        if (dao.count() > 0) return
        dao.insertAll(defaultArticles())
    }

    private fun defaultArticles(): List<ArticleEntity> = listOf(
        ArticleEntity(
            articleId = "a1",
            title = "💧 Stay Hydrated: Why Water Matters",
            summary = "Learn how much water you really need and how dehydration affects your body.",
            content = """
                Water makes up about 60% of your body. Every cell, tissue, and organ needs it to function.

                WHY IT MATTERS
                • Regulates body temperature
                • Lubricates joints
                • Protects your spinal cord and other sensitive tissues
                • Helps your kidneys flush out waste

                HOW MUCH DO YOU NEED?
                Most adults need 6–8 glasses per day. Hot weather, exercise, and illness increase this.
                A simple check: if your urine is pale yellow, you're well hydrated.

                TIPS
                1. Keep a bottle on your desk.
                2. Drink a glass with every meal.
                3. Add lemon or mint if plain water bores you.
                4. Eat water-rich foods: watermelon, cucumber, oranges.

                When to see a doctor: persistent dark urine, dizziness, or rapid heartbeat.
            """.trimIndent(),
            category = "Nutrition",
            author = "HealthEase Team",
            source = "WHO / SA DoH",
            imageEmoji = "💧",
            tags = "hydration,water,nutrition",
            readMinutes = 3
        ),
        ArticleEntity(
            articleId = "a2",
            title = "🥗 Eating Well on a Budget in SA",
            summary = "Balanced meals without breaking the bank — practical tips for every household.",
            content = """
                Eating well doesn't have to be expensive. Here's how to build balanced meals
                using affordable, locally available foods.

                BUILD A PLATE
                • ½ vegetables & fruit
                • ¼ whole grains (pap, brown rice, whole-wheat bread)
                • ¼ protein (beans, lentils, eggs, tinned fish, chicken)

                SMART SHOPPING
                • Buy seasonal produce — cheaper and tastier.
                • Dried beans and lentils cost less than meat and last longer.
                • Tinned fish (pilchards, tuna) is protein-rich and affordable.
                • Frozen vegetables are just as nutritious as fresh.

                LIMIT
                • Sugary drinks, deep-fried snacks, and processed meats.

                Remember: small changes add up. Start with one meal a day.
            """.trimIndent(),
            category = "Nutrition",
            author = "HealthEase Team",
            source = "SA Department of Health",
            imageEmoji = "🥗",
            tags = "nutrition,budget,meal-planning",
            readMinutes = 4
        ),
        ArticleEntity(
            articleId = "a3",
            title = "🏃 30 Minutes a Day Changes Everything",
            summary = "How regular physical activity improves heart health, mood, and sleep.",
            content = """
                You don't need a gym. 30 minutes of moderate activity, 5 days a week, is enough
                to see real benefits.

                BENEFITS
                • Lower blood pressure & cholesterol
                • Better mood and less anxiety
                • Stronger bones & muscles
                • Improved sleep
                • Weight management

                WHAT COUNTS?
                • Brisk walking
                • Dancing
                • Gardening
                • Cycling to the shops
                • Climbing stairs instead of taking the lift

                START SMALL
                Week 1: 10 minutes daily
                Week 2: 15 minutes
                Week 3: 20 minutes
                Week 4: 30 minutes

                Consistency beats intensity. Move every day!
            """.trimIndent(),
            category = "Fitness",
            author = "HealthEase Team",
            source = "WHO",
            imageEmoji = "🏃",
            tags = "fitness,exercise,walking",
            readMinutes = 3
        ),
        ArticleEntity(
            articleId = "a4",
            title = "🧘 Managing Stress: A 5-Minute Practice",
            summary = "Simple breathing and grounding techniques you can use anywhere.",
            content = """
                Stress is a normal response — but chronic stress harms your body.

                THE 4-7-8 BREATH
                1. Sit comfortably.
                2. Breathe in through your nose for 4 seconds.
                3. Hold for 7 seconds.
                4. Exhale slowly through your mouth for 8 seconds.
                5. Repeat 4 times.

                THE 5-4-3-2-1 GROUNDING
                Name:
                • 5 things you can see
                • 4 things you can touch
                • 3 things you can hear
                • 2 things you can smell
                • 1 thing you can taste

                WHEN TO SEEK HELP
                If you feel hopeless, can't sleep, or lose interest in things you love for
                more than 2 weeks — speak to a professional. SADAG helpline: 0800 456 789.
            """.trimIndent(),
            category = "Mental Health",
            author = "HealthEase Team",
            source = "SADAG",
            imageEmoji = "🧘",
            tags = "stress,mindfulness,mental-health",
            readMinutes = 4
        ),
        ArticleEntity(
            articleId = "a5",
            title = "🩸 Living Well With Diabetes",
            summary = "Daily habits that keep blood sugar stable and complications away.",
            content = """
                Diabetes is manageable. Small daily choices make a huge difference.

                EVERY DAY
                • Take your medication as prescribed.
                • Check your blood sugar if advised.
                • Eat meals at regular times.
                • Choose high-fibre foods: beans, oats, vegetables.
                • Walk for at least 30 minutes.
                • Check your feet for cuts or sores.

                LIMIT
                • Refined sugar, white bread, sugary drinks.
                • Alcohol (speak to your doctor about safe limits).

                WATCH FOR
                • Frequent urination, extreme thirst, blurred vision
                • Numbness in hands or feet
                • Wounds that don't heal

                Book regular check-ups — at least twice a year.
            """.trimIndent(),
            category = "Chronic Conditions",
            author = "HealthEase Team",
            source = "Diabetes SA",
            imageEmoji = "🩸",
            tags = "diabetes,chronic,blood-sugar",
            readMinutes = 5
        ),
        ArticleEntity(
            articleId = "a6",
            title = "❤️ Blood Pressure: Know Your Numbers",
            summary = "Understanding what your BP readings mean and how to keep them healthy.",
            content = """
                High blood pressure (hypertension) is often called the "silent killer" because
                it has no symptoms — but it damages your heart, brain, and kidneys over time.

                WHAT THE NUMBERS MEAN
                • Normal: below 120/80
                • Elevated: 120–129 / below 80
                • High Stage 1: 130–139 / 80–89
                • High Stage 2: 140+ / 90+

                HOW TO LOWER IT
                • Reduce salt (aim for less than 5g per day)
                • Eat more fruit and vegetables
                • Exercise 30 min most days
                • Limit alcohol
                • Don't smoke
                • Manage stress
                • Take medication exactly as prescribed

                CHECK IT
                Have your BP checked at any clinic or pharmacy — usually free.
            """.trimIndent(),
            category = "Chronic Conditions",
            author = "HealthEase Team",
            source = "Heart & Stroke Foundation SA",
            imageEmoji = "❤️",
            tags = "blood-pressure,heart,hypertension",
            readMinutes = 4
        ),
        ArticleEntity(
            articleId = "a7",
            title = "😴 Sleep: Your Body's Repair Shop",
            summary = "Why 7–9 hours matters and how to actually get them.",
            content = """
                Sleep is when your body repairs itself. Skimping on it raises risk of
                heart disease, diabetes, and depression.

                HOW MUCH?
                Adults: 7–9 hours
                Teens: 8–10 hours
                Children: 9–12 hours

                GOOD SLEEP HYGIENE
                1. Same bedtime and wake time — even weekends.
                2. No screens 1 hour before bed.
                3. Cool, dark, quiet room.
                4. No caffeine after 2pm.
                5. No heavy meals within 2 hours of bed.
                6. Exercise during the day, not right before bed.

                SEE A DOCTOR IF
                You snore heavily, wake gasping, or feel tired despite 8 hours.
            """.trimIndent(),
            category = "General Wellness",
            author = "HealthEase Team",
            source = "WHO",
            imageEmoji = "😴",
            tags = "sleep,wellness,rest",
            readMinutes = 3
        ),
        ArticleEntity(
            articleId = "a8",
            title = "🚭 Quitting Smoking: Start Today",
            summary = "What happens to your body in the first year after your last cigarette.",
            content = """
                It's never too late to quit. Your body starts healing within hours.

                TIMELINE
                • 20 min: heart rate & BP drop
                • 12 hrs: carbon monoxide levels normalise
                • 2 weeks–3 months: circulation & lung function improve
                • 1–9 months: coughing & shortness of breath decrease
                • 1 year: risk of heart disease halved

                HOW TO QUIT
                1. Set a quit date.
                2. Tell friends and family.
                3. Remove cigarettes, lighters, ashtrays.
                4. Identify triggers (coffee, stress) and plan alternatives.
                5. Use nicotine replacement if needed.
                6. Call the National Quitline: 011 720 3145.

                Relapse is normal. Try again. Every attempt gets you closer.
            """.trimIndent(),
            category = "General Wellness",
            author = "HealthEase Team",
            source = "National Council Against Smoking",
            imageEmoji = "🚭",
            tags = "smoking,quit,wellness",
            readMinutes = 3
        ),
        ArticleEntity(
            articleId = "a9",
            title = "💉 Vaccines: Your Shield",
            summary = "Which vaccines adults in SA should keep up to date.",
            content = """
                Vaccines protect you AND those around you.

                RECOMMENDED FOR ADULTS
                • Flu: every year
                • COVID-19: per current SA DoH guidance
                • Tetanus: every 10 years
                • HPV: for young adults if not yet vaccinated

                FOR OLDER ADULTS (65+)
                • Pneumococcal
                • Shingles

                FOR PREGNANT WOMEN
                • Tdap (whooping cough)
                • Flu

                WHERE
                Most public clinics offer these free. Bring your ID and vaccination card.

                Side effects are usually mild: sore arm, low fever. Serious reactions are rare.
            """.trimIndent(),
            category = "General Wellness",
            author = "HealthEase Team",
            source = "SA Department of Health",
            imageEmoji = "💉",
            tags = "vaccines,prevention,immunisation",
            readMinutes = 3
        ),
        ArticleEntity(
            articleId = "a10",
            title = "🦠 Cold vs Flu vs COVID: Know the Difference",
            summary = "Similar symptoms, different causes — how to tell them apart.",
            content = """
                All three are respiratory illnesses, but severity and treatment differ.

                COMMON COLD
                • Gradual onset
                • Runny nose, sneezing, mild sore throat
                • Rarely fever
                • No body aches usually

                FLU
                • Sudden onset
                • High fever, body aches, chills
                • Dry cough, headache
                • Can be severe

                COVID-19
                • Range from mild to severe
                • Loss of taste or smell is a clue
                • Shortness of breath
                • Can have no symptoms at all

                WHAT TO DO
                • Rest, fluids, paracetamol for fever.
                • Isolate if you suspect flu or COVID.
                • Seek care if: difficulty breathing, chest pain, confusion.

                Vaccination reduces severity for both flu and COVID.
            """.trimIndent(),
            category = "COVID-19",
            author = "HealthEase Team",
            source = "WHO",
            imageEmoji = "🦠",
            tags = "cold,flu,covid",
            readMinutes = 4
        ),
        ArticleEntity(
            articleId = "a11",
            title = "🌸 Women's Health: Cervical Screening Saves Lives",
            summary = "Pap smears and HPV tests — when to start and how often.",
            content = """
                Cervical cancer is one of the most preventable cancers.

                SCREENING GUIDELINES (SA)
                • Start at age 25 (or earlier if sexually active).
                • Pap smear every 3 years (or HPV test every 5 years).
                • Continue until age 65 if results are normal.

                WHAT HAPPENS
                A quick, painless swab of the cervix. Takes 5 minutes.

                SYMPTOMS TO NEVER IGNORE
                • Bleeding between periods or after sex
                • Unusual discharge
                • Pain during sex
                • Pelvic pain

                WHERE
                Free at most public clinics. Book at your local clinic.

                Regular screening + HPV vaccination = best protection.
            """.trimIndent(),
            category = "Women's Health",
            author = "HealthEase Team",
            source = "SA Department of Health",
            imageEmoji = "🌸",
            tags = "women,cervical,screening",
            readMinutes = 3
        ),
        ArticleEntity(
            articleId = "a12",
            title = "🧔 Men's Health: The Check-Up You Keep Skipping",
            summary = "Prostate, heart, and mental health — what every man over 40 should know.",
            content = """
                Men are less likely to see a doctor — and it costs them.

                AFTER 40, CHECK
                • Blood pressure: every year
                • Cholesterol: every 3–5 years
                • Blood sugar: every 3 years
                • Prostate: discuss with your doctor from age 45

                WARNING SIGNS
                • Chest pain, shortness of breath
                • Trouble urinating, weak stream
                • Persistent back pain
                • Unexplained weight loss
                • Loss of interest in life

                MENTAL HEALTH MATTERS
                Men are 4x more likely to die by suicide in SA. Speaking up is strength.
                Helpline: SADAG 0800 456 789.

                Book that check-up this month.
            """.trimIndent(),
            category = "Men's Health",
            author = "HealthEase Team",
            source = "SA Men's Health Forum",
            imageEmoji = "🧔",
            tags = "men,prostate,checkup",
            readMinutes = 4
        ),
        ArticleEntity(
            articleId = "a13",
            title = "👵 Healthy Ageing: 6 Pillars for Seniors",
            summary = "Small daily habits that keep older adults independent and vibrant.",
            content = """
                Ageing well isn't about avoiding it — it's about thriving through it.

                6 PILLARS
                1. MOVE: Walk, stretch, garden. 30 min daily.
                2. EAT: Protein at every meal, calcium for bones, plenty of water.
                3. CONNECT: Loneliness harms health as much as smoking. Stay social.
                4. SLEEP: 7–8 hours. Naps are fine (20 min max).
                5. THINK: Read, puzzle, learn something new.
                6. CHECK: Annual doctor visit. Medications reviewed yearly.

                FALL PREVENTION
                • Remove loose rugs
                • Good lighting
                • Grab bars in bathroom
                • Regular eye checks

                Live fully at every age. 💚
            """.trimIndent(),
            category = "Senior Health",
            author = "HealthEase Team",
            source = "Age-in-Action SA",
            imageEmoji = "👵",
            tags = "seniors,ageing,wellness",
            readMinutes = 4
        ),
        ArticleEntity(
            articleId = "a14",
            title = "🧼 Hand Hygiene: 20 Seconds That Save Lives",
            summary = "The simplest health habit you can build today.",
            content = """
                Handwashing is the single most effective way to prevent infection.

                WHEN TO WASH
                • Before eating or cooking
                • After using the toilet
                • After coughing, sneezing, or blowing your nose
                • After touching shared surfaces
                • Before and after caring for a sick person

                HOW
                1. Wet hands with clean water.
                2. Apply soap.
                3. Rub for at least 20 seconds — palms, backs, between fingers, under nails.
                4. Rinse.
                5. Dry with a clean towel.

                IF NO SOAP
                Use hand sanitiser with at least 60% alcohol.

                20 seconds. Every time. Saves lives.
            """.trimIndent(),
            category = "General Wellness",
            author = "HealthEase Team",
            source = "WHO",
            imageEmoji = "🧼",
            tags = "hygiene,prevention,handwashing",
            readMinutes = 2
        ),
        ArticleEntity(
            articleId = "a15",
            title = "🩹 First Aid Basics Everyone Should Know",
            summary = "How to respond to the 5 most common emergencies.",
            content = """
                Know what to do before help arrives.

                1. BLEEDING
                Apply firm pressure with a clean cloth. Don't remove it. Elevate if possible.

                2. BURNS
                Cool with running water for 20 minutes. Do NOT use ice, butter, or toothpaste.
                Cover loosely.

                3. CHOKING
                Encourage coughing. If severe, give 5 back blows between the shoulder blades.
                If adult is conscious, alternate with abdominal thrusts.

                4. FAINTING
                Lay person flat, raise legs, loosen tight clothing. Turn head to side.

                5. HEART ATTACK
                Call 10177 (ambulance). Chew an aspirin if not allergic. Keep the person calm
                and sitting.

                Numbers to save:
                • 10177 — Ambulance
                • 112 — Mobile emergency
                • 10111 — Police
            """.trimIndent(),
            category = "General Wellness",
            author = "HealthEase Team",
            source = "SA Red Cross",
            imageEmoji = "🩹",
            tags = "first-aid,emergency,safety",
            readMinutes = 4
        )
    )
}