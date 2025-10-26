package se331.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import se331.backend.dao.NewsDao;
import se331.backend.entity.Comment;
import se331.backend.entity.News;
import se331.backend.entity.Vote;
import se331.backend.security.user.Role;
import se331.backend.security.user.User;
import se331.backend.security.user.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
public class InitApp implements CommandLineRunner {

    @Autowired
    private NewsDao newsDao;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // เรียก initDefaultUsers (เพื่อให้มี User ก่อนสร้าง News)
        initDefaultUsers();

        // สร้าง News
        if (newsDao.findAll().isEmpty()) {
            System.out.println("No news found. Initializing sample data...");
            initSampleData();
        } else {
            System.out.println("Database already populated (News).");
        }
    }

    private void initDefaultUsers() {
        System.out.println("Initializing default users...");
        seedUserIfMissing(
                "admin",
                "admin@app.com",
                "Admin",
                "User",
                "admin",
                "https://placehold.co/100x100/FF0000/FFF?text=ADMIN",
                List.of(Role.ROLE_ADMIN, Role.ROLE_MEMBER, Role.ROLE_READER) // Admin มีทุก Role
        );
        seedUserIfMissing(
                "member",
                "member@app.com",
                "Member",
                "User",
                "member",
                "https://placehold.co/100x100/00FF00/FFF?text=MEMBER",
                List.of(Role.ROLE_MEMBER, Role.ROLE_READER) // Member มี 2 Roles
        );
        seedUserIfMissing(
                "reader",
                "reader@app.com",
                "Reader",
                "User",
                "reader",
                "https://placehold.co/100x100/0000FF/FFF?text=READER",
                List.of(Role.ROLE_READER) // Reader มี Role เดียว
        );
        System.out.println("Default users initialization check complete.");
    }

    private void seedUserIfMissing(
            String username,
            String email,
            String firstname,
            String lastname,
            String rawPassword,
            String profileImage,
            List<Role> roles // รับเป็น List
    ) {
        Optional<User> existing = userRepository.findByUsername(username);
        if (existing.isPresent()) {
            System.out.println("User '" + username + "' already exists.");
            return; // ถ้ามีอยู่แล้ว ไม่ต้องทำอะไร
        }

        // ถ้ายังไม่มี ให้สร้างใหม่
        User user = User.builder()
                .username(username)
                .email(email)
                .firstname(firstname)
                .lastname(lastname)
                .password(passwordEncoder.encode(rawPassword))
                .profileImage(profileImage)
                .roles(roles)
                .enabled(true)
                .build();

        userRepository.save(user);
        System.out.println("Created user: " + username + " with roles: " + roles);
    }

    private void initSampleData() {
        // --- ข่าวที่ 1: Ocean Cleanup Project ---
        News news1 = News.builder()
                .topic("Ocean Cleanup Project Removes 1,000 Tons of Plastic")
                .shortDetail("The Ocean Cleanup Foundation reached a milestone by removing over 1,000 tons of plastic from the Great Pacific Garbage Patch using their System 03 technology, marking the largest single-operation ocean plastic removal in history.")
                .fullDetail("In a groundbreaking achievement for marine environmental protection, The Ocean Cleanup Foundation reported that their latest System 03 vessel has successfully removed more than 1,000 tons of plastic debris from the Great Pacific Garbage Patch over the past six months, representing the most significant ocean plastic extraction operation to date. The innovative system, which uses advanced AI-guided collection nets and solar-powered propulsion, has demonstrated unprecedented efficiency in targeting microplastics and larger debris while minimizing impact on marine life through sophisticated bypass mechanisms. Founded by Dutch inventor Boyan Slat, the organization has been working since 2018 to develop scalable solutions for ocean plastic pollution, with this milestone representing a 300% improvement in collection rates compared to previous systems. Marine biologist Dr. Sarah Chen from the Pacific Marine Research Institute praised the achievement, stating that the removal of this volume of plastic could prevent the deaths of an estimated 50,000 marine animals annually, while oceanographer Professor James Mitchell noted that the project's data collection capabilities are providing valuable insights into ocean current patterns and plastic distribution. The collected plastic is being processed into consumer products through partnerships with major manufacturers, creating a circular economy model that the foundation plans to expand globally, with five additional System 03 vessels scheduled for deployment by 2026 across the world's most polluted ocean regions.")
                .image("https://assets.theoceancleanup.com/scaled/1200x675/app/uploads/2023/09/System03_Full_Span-scaled.jpg")
                .reporter("Robert Clark")
                .dateTime(Instant.parse("2025-08-09T11:00:00Z"))
                .realVotes(2)
                .fakeVotes(14)
                .build();

        Comment c1_1 = Comment.builder()
                .username("Jack Phillips")
                .text("Feels too good to be true, I'm skeptical.")
                .image(null)
                .time(Instant.parse("2025-08-09T15:00:00Z"))
                .vote(Vote.FAKE)
                .news(news1)
                .build();

        Comment c1_2 = Comment.builder()
                .username("Zoey Adams")
                .text("Love seeing technology used for environmental impact.")
                .image("https://i.pinimg.com/736x/24/f2/50/24f250c18fd7d70e15db4fb8aead4619.jpg")
                .time(Instant.parse("2025-08-09T13:00:00Z"))
                .vote(Vote.REAL)
                .news(news1)
                .build();

        Comment c1_3 = Comment.builder()
                .username("Sirilak Srisuk")
                .text("Until independent reports confirm, I won't fully trust this.")
                .image(null)
                .time(Instant.parse("2025-08-09T13:00:00Z"))
                .vote(Vote.FAKE)
                .news(news1)
                .build();

        Comment c1_4 = Comment.builder()
                .username("Tanaka Ayaka")
                .text("Officials provide updates on the situation.")
                .image("https://grist.org/wp-content/uploads/2019/10/theoceancleanup_october2nd_press_briefing_system001b-30-e1570213626974.jpg")
                .time(Instant.parse("2025-08-09T16:00:00Z"))
                .vote(Vote.REAL)
                .news(news1)
                .build();

        news1.getComments().add(c1_1);
        news1.getComments().add(c1_2);
        news1.getComments().add(c1_3);
        news1.getComments().add(c1_4);
        newsDao.save(news1);

        // --- ข่าวที่ 2: New Exoplanet ---
        News news2 = News.builder()
                .topic("Scientists Discover New Exoplanet Similar to Earth")
                .shortDetail("International astronomers discover Kepler-452c, an Earth-like exoplanet 60% larger than Earth located in the habitable zone of a sun-like star, potentially capable of supporting liquid water and life.")
                .fullDetail("A team of international astronomers has made a groundbreaking discovery of Kepler-452c, a remarkable exoplanet located in the habitable zone of a star strikingly similar to our Sun, marking one of the most significant findings in the search for extraterrestrial life. The planet, approximately 60% larger than Earth with a rocky surface composition, orbits within the critical Goldilocks zone where temperatures allow liquid water to exist - a fundamental requirement for life as we know it. Advanced data analysis from the Kepler Space Telescope revealed that Kepler-452c is estimated to be 6 billion years old, making it significantly older than Earth and offering scientists a unique laboratory to study long-term planetary evolution and potential life development. Preliminary atmospheric observations suggest the planet may possess conditions capable of supporting life, though researchers emphasize that extensive follow-up studies are needed to confirm these promising indicators. Dr. Maria Rodriguez, lead astronomer on the discovery team, stated that this finding represents a major leap forward in our understanding of planetary habitability and brings us closer than ever to answering whether we are alone in the universe. The discovery not only advances our knowledge of planetary formation but also establishes crucial groundwork for future space missions designed to detect biosignatures and explore potentially habitable worlds beyond our solar system.")
                .image("https://science.nasa.gov/wp-content/uploads/2024/01/toi715b1280-illo.jpg?resize=900,506")
                .reporter("Emily Garcia")
                .dateTime(Instant.parse("2025-08-14T16:27:45.406644Z"))
                .realVotes(210)
                .fakeVotes(25)
                .build();

        Comment c2_1 = Comment.builder()
                .username("Guillaume Leclerc")
                .text("Wow! This could change how we think about habitable planets. 🚀")
                .image("https://www.universetoday.com/article_images/helix-nebula.jpeg")
                .time(Instant.parse("2025-08-14T13:15:00Z"))
                .vote(Vote.REAL)
                .news(news2)
                .build();

        Comment c2_2 = Comment.builder()
                .username("Su young")
                .text("Another Earth-like planet? We've seen this before, nothing new.")
                .image(null)
                .time(Instant.parse("2025-08-14T13:50:00Z"))
                .vote(Vote.FAKE)
                .news(news2)
                .build();

        news2.getComments().add(c2_1);
        news2.getComments().add(c2_2);
        newsDao.save(news2);

        // --- ข่าวที่ 3: Cancer Treatment ---
        News news3 = News.builder()
                .topic("Major Breakthrough in Cancer Treatment Announced")
                .shortDetail("Scientists at Global Health Institute develop revolutionary cancer therapy combining CRISPR gene editing and personalized immunotherapy, achieving 85% response rates in early trials and offering new hope for advanced-stage cancer patients.")
                .fullDetail("Researchers at the Global Health Institute have announced a revolutionary cancer treatment breakthrough that combines cutting-edge CRISPR gene editing technology with personalized immunotherapy, demonstrating unprecedented success in early clinical trials with an 85% response rate among advanced-stage cancer patients. The innovative therapy works by precisely editing a patient's immune cells to enhance their ability to recognize and destroy cancer cells while leaving healthy tissue completely unharmed, addressing one of the most challenging aspects of traditional cancer treatment. Unlike conventional chemotherapy and radiation that often cause severe side effects, this personalized approach trains each patient's own immune system to target malignant cells with surgical precision, dramatically reducing adverse reactions and improving quality of life during treatment. Dr. Sarah Kim, lead researcher and oncologist, explained that this represents a paradigm shift from attacking cancer with external toxins to empowering the body's natural defense mechanisms with molecular-level precision. The treatment protocol involves extracting immune cells from patients, genetically modifying them in specialized laboratories, and reintroducing them to fight cancer more effectively than ever before. Phase II trials involving 300 patients across multiple cancer types are scheduled to begin next year, with researchers optimistic that the therapy could receive regulatory approval within five years and become a standard treatment option, potentially transforming cancer from a terminal diagnosis into a manageable condition for millions of patients worldwide.")
                .image("https://assets.weforum.org/article/image/23tEFwxzFGXY0PkZPE_0zVBrK-86zupQa99xMOKhxqY.jpg")
                .reporter("Michael Lee")
                .dateTime(Instant.parse("2025-08-14T16:27:45.406652Z"))
                .realVotes(300)
                .fakeVotes(12)
                .build();

        Comment c3_1 = Comment.builder()
                .username("David Kim")
                .text("This gives hope to millions of patients worldwide. ❤️")
                .image(null)
                .time(Instant.parse("2025-08-14T15:00:00Z"))
                .vote(Vote.REAL)
                .news(news3)
                .build();

        Comment c3_2 = Comment.builder()
                .username("Margaux Fontaine")
                .text("Can't wait for this to be available to everyone.")
                .image("https://i.pinimg.com/736x/ba/92/7f/ba927ff34cd961ce2c184d47e8ead9f6.jpg")
                .time(Instant.parse("2025-08-14T15:40:00Z"))
                .vote(Vote.REAL)
                .news(news3)
                .build();

        news3.getComments().add(c3_1);
        news3.getComments().add(c3_2);
        newsDao.save(news3);

        // --- ข่าวที่ 4: SpaceX Launch ---
        News news4 = News.builder()
                .topic("SpaceX Successfully Launches New Rocket")
                .shortDetail("SpaceX achieves its 20th successful launch of 2025 with the debut flight of its enhanced Falcon Heavy Block 6, deploying three commercial satellites and testing revolutionary methane-based propulsion technology.")
                .fullDetail("SpaceX marked another historic milestone early this morning with the flawless launch of its enhanced Falcon Heavy Block 6 rocket from Kennedy Space Center, achieving the company's 20th successful mission of 2025 and demonstrating breakthrough advancements in space transportation technology. The dawn launch, witnessed by over 15,000 spectators gathered along Florida's Space Coast, successfully deployed three commercial satellites into precise orbits while testing revolutionary methane-based Raptor engines designed to significantly reduce launch costs and increase payload capacity by 40%. The mission showcased SpaceX's new autonomous flight termination system and advanced grid fins made from titanium, technologies that will be crucial for the company's ambitious Artemis lunar missions and Mars colonization program. CEO Elon Musk celebrated the achievement on social media, stating that today's success brings us one step closer to making life multi-planetary, while SpaceX engineers confirmed that all three booster cores successfully returned to Earth and landed simultaneously at Cape Canaveral, setting a new record for precision recovery operations. Industry analysts note that this technological leap strengthens SpaceX's dominant position in the $400 billion global space economy, with the company now holding contracts worth over $10 billion for upcoming NASA missions, commercial satellite deployments, and space tourism ventures. The successful integration of these next-generation systems positions SpaceX to begin crewed lunar missions by 2027 and establishes the foundation for the planned Mars missions scheduled for the 2030s, marking a pivotal moment in humanity's expansion beyond Earth.")
                .image("https://spaceflightnow.com/wp-content/uploads/2023/05/20230510star2-9.jpeg")
                .reporter("John Lee")
                .dateTime(Instant.parse("2025-08-14T16:27:45.406667Z"))
                .realVotes(250)
                .fakeVotes(8)
                .build();

        Comment c4_1 = Comment.builder()
                .username("SpaceFan99")
                .text("Amazing! Space travel is really becoming normal.")
                .image(null)
                .time(Instant.parse("2025-08-14T16:30:00Z"))
                .vote(Vote.REAL)
                .news(news4)
                .build();

        Comment c4_2 = Comment.builder()
                .username("Skeptic101")
                .text("Photos might be edited, verify the source.")
                .image(null)
                .time(Instant.parse("2025-08-14T16:50:00Z"))
                .vote(Vote.FAKE)
                .news(news4)
                .build();

        news4.getComments().add(c4_1);
        news4.getComments().add(c4_2);
        newsDao.save(news4);

        // --- ข่าวที่ 5: Major Flood ---
        News news5 = News.builder()
                .topic("Major Flood Hits Downtown City")
                .shortDetail("Heavy rainfall causes severe flooding in downtown city, prompting evacuations and widespread damage to infrastructure.")
                .fullDetail("Record-breaking rainfall has caused severe flooding in downtown areas, submerging roads, inundating buildings, and causing significant damage to local infrastructure. The floodwaters have disrupted transportation, leaving many roads impassable and stranding residents. Emergency services have evacuated over 5,000 people from affected areas, while local shelters have been set up to provide temporary housing, food, and medical aid. Authorities have warned of the heightened risk of waterborne diseases, urging citizens to stay away from floodwaters and remain in safe locations. In addition to immediate relief efforts, teams are distributing clean water, food, and medical supplies to those in need. Experts are raising concerns that climate change has led to more frequent and intense storms, and they are urging governments to prioritize urban planning reforms to reduce the risk of future disasters. The devastation has left many people without power and basic necessities, and recovery efforts are expected to continue for weeks.")
                .image("https://th-i.thgim.com/public/incoming/arg1a9/article68392085.ece/alternates/FREE_1200/PTI07_11_2024_000035A.jpg")
                .reporter("Emily Garcia")
                .dateTime(Instant.parse("2025-08-14T16:27:45.406671Z"))
                .realVotes(180)
                .fakeVotes(20)
                .build();

        Comment c5_1 = Comment.builder()
                .username("Alex Rivers")
                .text("Hope everyone affected stays safe. Sending thoughts!")
                .image(null)
                .time(Instant.parse("2025-08-14T18:00:00Z"))
                .vote(Vote.REAL)
                .news(news5)
                .build();

        Comment c5_2 = Comment.builder()
                .username("DoubtfulReader")
                .text("I think some reports are exaggerated, check local sources.")
                .image(null)
                .time(Instant.parse("2025-08-14T18:30:00Z"))
                .vote(Vote.FAKE)
                .news(news5)
                .build();

        news5.getComments().add(c5_1);
        news5.getComments().add(c5_2);
        newsDao.save(news5);

        // --- ข่าวที่ 6: Stock Market ---
        News news6 = News.builder()
                .topic("Stock Market Hits Record High Amid Tech Surge")
                .shortDetail("Global markets hit record highs as tech giants exceed Q3 earnings expectations, with Nasdaq surging 4.2% and AI stocks gaining $800 billion in value.")
                .fullDetail("Global financial markets celebrated a historic milestone today as major stock indices soared to unprecedented levels, led by an explosive rally in technology stocks that saw the Nasdaq Composite Index surge 4.2% to close at an all-time high of 18,750 points. The remarkable rally was fueled by blockbuster third-quarter earnings reports from tech giants including Apple, Microsoft, Google, and NVIDIA, which collectively exceeded analyst expectations by an average of 22%, generating over $800 billion in additional market capitalization for the artificial intelligence and renewable technology sectors. Investor enthusiasm reached fever pitch as AI-focused companies reported revenue growth exceeding 150% year-over-year, while clean energy stocks benefited from massive government infrastructure investments and corporate sustainability commitments. Market analyst Sarah Williams from Goldman Sachs commented that we're witnessing a fundamental shift in how investors value technology innovation, with AI and green tech now representing the primary drivers of economic growth for the next decade. However, veteran financial advisors are urging caution amid the exuberance, noting that rapid market gains often precede periods of increased volatility, and recommending that investors maintain diversified portfolios while avoiding overexposure to high-growth tech stocks. Trading volumes hit record levels with over $2.5 trillion in daily transactions, while institutional investors poured $45 billion into technology-focused ETFs this week alone, signaling sustained confidence in the sector's long-term prospects despite warnings about potential market corrections in the coming quarters.")
                .image("https://www.investopedia.com/thmb/L6KR0VU3zbZf8_ZBBuazL_UsRn8=/750x0/filters:no_upscale():max_bytes(150000):strip_icc():format(webp)/GettyImages-2229156936-61795d1806f6492ab9de6ba6212917fc.jpg")
                .reporter("Michael Brown")
                .dateTime(Instant.parse("2025-08-14T16:27:45.406674Z"))
                .realVotes(220)
                .fakeVotes(15)
                .build();

        Comment c6_1 = Comment.builder()
                .username("InvestorMike")
                .text("Great news for the economy, looking to invest more!")
                .image(null)
                .time(Instant.parse("2025-08-14T19:00:00Z"))
                .vote(Vote.REAL)
                .news(news6)
                .build();

        Comment c6_2 = Comment.builder()
                .username("CautiousAnalyst")
                .text("Be careful, markets might correct soon.")
                .image(null)
                .time(Instant.parse("2025-08-14T19:20:00Z"))
                .vote(Vote.FAKE)
                .news(news6)
                .build();

        news6.getComments().add(c6_1);
        news6.getComments().add(c6_2);
        newsDao.save(news6);

        // --- ข่าวที่ 7: Football Championship ---
        News news7 = News.builder()
                .topic("Local Football Team Wins National Championship")
                .shortDetail("City Eagles defeat defending champions 28-21 in overtime thriller to claim their first national championship in 15 years, sparking massive celebrations across the city.")
                .fullDetail("The City Eagles captured their first national championship in 15 years with a dramatic 28-21 overtime victory over the defending champion Metro Lions at National Stadium, capping off a perfect 14-0 season that has electrified the entire community and established the team as one of the greatest in franchise history. The nail-biting final saw quarterback Jake Morrison throw the game-winning touchdown pass with just 2:47 remaining in overtime, while the Eagles' defense intercepted three crucial passes to seal the victory before a sellout crowd of 75,000 fans and millions watching nationwide. Within minutes of the final whistle, over 100,000 jubilant fans flooded downtown streets in spontaneous celebrations that continued past midnight, prompting Mayor Patricia Johnson to declare Monday a city holiday and announce plans for a championship parade expected to draw 500,000 people. Head coach Michael Rodriguez, who led the team's remarkable turnaround from last place just three years ago, praised his players unbreakable spirit and dedication, while star running back Tommy Chen was named championship MVP after rushing for 185 yards and two touchdowns. The victory has generated an estimated $50 million economic boost for the city through increased tourism, merchandise sales, and national media attention, while inspiring thousands of young athletes in local youth programs who see the Eagles as proof that dreams can become reality through hard work and determination.")
                .image("https://vtdigger.org/wp-content/uploads/2025/08/vermont-green-2-20250802-2000x1330.jpg")
                .reporter("Emily Miller")
                .dateTime(Instant.parse("2025-08-14T16:27:45.406677Z"))
                .realVotes(300)
                .fakeVotes(5)
                .build();

        Comment c7_1 = Comment.builder()
                .username("SoccerFan88")
                .text("Incredible match! Fans are ecstatic!")
                .image(null)
                .time(Instant.parse("2025-08-14T20:00:00Z"))
                .vote(Vote.REAL)
                .news(news7)
                .build();

        Comment c7_2 = Comment.builder()
                .username("DoubterGuy")
                .text("Maybe highlights are exaggerated.")
                .image(null)
                .time(Instant.parse("2025-08-14T20:30:00Z"))
                .vote(Vote.FAKE)
                .news(news7)
                .build();

        news7.getComments().add(c7_1);
        news7.getComments().add(c7_2);
        newsDao.save(news7);

        // --- ข่าวที่ 8: Art Exhibition ---
        News news8 = News.builder()
                .topic("New Art Exhibition Opens Downtown")
                .shortDetail("Voices of Tomorrow art exhibition opens at Metropolitan Gallery, featuring 150 works from 45 local and international artists exploring themes of urban transformation and cultural identity.")
                .fullDetail("The highly anticipated Voices of Tomorrow art exhibition opened its doors at the prestigious Metropolitan Gallery downtown, drawing over 2,000 visitors on opening night to experience an extraordinary collection of 150 contemporary artworks from 45 emerging local artists and internationally acclaimed figures including renowned sculptor Maria Santos and digital artist Chen Wei-Ming. The groundbreaking exhibition, curated by Dr. Amanda Foster, explores powerful themes of urban transformation, climate resilience, and cultural diversity through a stunning array of interactive installations, immersive digital displays, and thought-provoking mixed-media pieces that invite visitors to engage directly with pressing social and environmental issues. Highlights include a 20-foot kinetic sculpture representing rising sea levels, a virtual reality experience showcasing the city's evolution over the past century, and a collaborative mural created by 12 local high school students that celebrates the community's multicultural heritage. Gallery director Robert Kim announced that the two-month exhibition will feature an extensive program of weekly artist talks, hands-on workshops for all ages, and specialized guided tours led by the artists themselves, with proceeds from artwork sales supporting the city's emerging artist grant program. The opening weekend alone generated $75,000 in art sales and attracted cultural tourists from neighboring states, positioning the exhibition as a significant cultural and economic catalyst for the downtown arts district while providing a vital platform for local creative talent to gain national recognition.")
                .image("https://culture360.asef.org/media/2017/11/295chD8_germanart_loans05.jpg")
                .reporter("Michael Davis")
                .dateTime(Instant.parse("2025-08-14T16:27:45.406683Z"))
                .realVotes(110)
                .fakeVotes(8)
                .build();

        Comment c8_1 = Comment.builder()
                .username("Ratti P.")
                .text("Beautiful curation! Definitely worth visiting.")
                .image(null)
                .time(Instant.parse("2025-08-14T10:00:00Z"))
                .vote(Vote.REAL)
                .news(news8)
                .build();

        Comment c8_2 = Comment.builder()
                .username("SkepticalViewer")
                .text("Some pieces look exaggerated, not sure if authentic.")
                .image(null)
                .time(Instant.parse("2025-08-14T10:20:00Z"))
                .vote(Vote.FAKE)
                .news(news8)
                .build();

        news8.getComments().add(c8_1);
        news8.getComments().add(c8_2);
        newsDao.save(news8);

        // --- ข่าวที่ 9: Tech Conference ---
        News news9 = News.builder()
                .topic("Tech Conference Announced with Key Industry Leaders")
                .shortDetail("TechGlobal Summit 2025 announces star-studded lineup featuring Elon Musk, Satya Nadella, and 200+ industry leaders for three-day innovation showcase expected to draw 25,000 attendees.")
                .fullDetail("The prestigious TechGlobal Summit 2025 has officially announced its impressive lineup of keynote speakers and industry leaders, scheduled for September 15-17 at the Los Angeles Convention Center, with confirmed appearances by Tesla CEO Elon Musk, Microsoft CEO Satya Nadella, OpenAI's Sam Altman, and over 200 technology executives representing companies valued at more than $2 trillion collectively. The three-day innovation showcase is expected to attract 25,000 participants from 80 countries, including venture capitalists managing $500 billion in tech investments, startup founders, and government officials focused on digital transformation and technology policy. Conference highlights include Musk's highly anticipated keynote on The Future of Autonomous Systems, panel discussions on AI regulation featuring top government officials, cybersecurity workshops led by former NSA experts, and a sustainability tech pavilion showcasing solutions for climate change mitigation. Event organizer Jennifer Park, CEO of Global Tech Events, emphasized that this year's summit represents the largest gathering of technology leaders since the pandemic, creating unprecedented opportunities for collaboration, investment, and breakthrough innovations that will shape the next decade. The conference will feature a startup pitch competition with $10 million in total prize money, exclusive investor networking sessions, and live demonstrations of cutting-edge technologies including quantum computing, brain-computer interfaces, and next-generation renewable energy systems, establishing the summit as the premier destination for anyone seeking to understand and influence the future of technology.")
                .image("https://www.traveldailymedia.com/assets/2020/01/techsauce.jpg")
                .reporter("Sarah Williams")
                .dateTime(Instant.parse("2025-08-14T16:27:45.406687Z"))
                .realVotes(180)
                .fakeVotes(12)
                .build();

        Comment c9_1 = Comment.builder()
                .username("TechFan99")
                .text("Can't wait! Looks like a great lineup of speakers.")
                .image(null)
                .time(Instant.parse("2025-08-14T11:30:00Z"))
                .vote(Vote.REAL)
                .news(news9)
                .build();

        Comment c9_2 = Comment.builder()
                .username("CautiousReporter")
                .text("Check official site before registering, sometimes announcements are exaggerated.")
                .image(null)
                .time(Instant.parse("2025-08-14T11:50:00Z"))
                .vote(Vote.FAKE)
                .news(news9)
                .build();

        news9.getComments().add(c9_1);
        news9.getComments().add(c9_2);
        newsDao.save(news9);

        // --- ข่าวที่ 10: Café Chain Expansion ---
        News news10 = News.builder()
                .topic("New Café Chain Expands Internationally")
                .shortDetail("Popular local café chain announces $250 million international expansion, opening 150 stores across 12 countries with plans to compete directly with Starbucks in the global market.")
                .fullDetail("The rapidly growing artisanal café chain that started as a single coffee shop just eight years ago announced its ambitious $250 million international expansion plan that will see 150 flagship stores opening across 12 countries in Europe and Asia over the next 18 months, positioning the brand as a serious challenger to industry giants like Starbucks and Costa Coffee. The expansion strategy begins with premium locations in London, Paris, Tokyo, Seoul, and Singapore, featuring locally-sourced ingredients and regionally-adapted menu items such as matcha-infused pastries in Japan and Nordic-inspired breakfast bowls in Scandinavian markets, while maintaining the brand's signature commitment to fair-trade coffee and zero-waste operations. CEO Maria Rodriguez revealed that the company has secured strategic partnerships with local suppliers in each target market and invested heavily in sustainable packaging technology, including compostable cups and plant-based food containers that align with European environmental regulations. The international rollout will introduce the company's innovative Coffee Passport loyalty program, allowing customers to earn rewards across all global locations, alongside a cutting-edge mobile ordering system that uses AI to predict customer preferences and reduce wait times. Industry analysts project that this bold expansion could capture 3% of the global specialty coffee market within five years, potentially generating over $1 billion in annual revenue and establishing the café chain as the first American coffee brand to successfully challenge established European coffee culture while maintaining its commitment to environmental sustainability and community engagement.")
                .image("https://perfectdailygrind.com/wp-content/uploads/2022/10/airport-coffee-1024x640.jpg")
                .reporter("Robert Garcia")
                .dateTime(Instant.parse("2025-08-14T16:27:45.406690Z"))
                .realVotes(140)
                .fakeVotes(10)
                .build();

        Comment c10_1 = Comment.builder()
                .username("CoffeeLover")
                .text("Exciting! I hope they come to my city soon.")
                .image(null)
                .time(Instant.parse("2025-08-14T12:10:00Z"))
                .vote(Vote.REAL)
                .news(news10)
                .build();

        Comment c10_2 = Comment.builder()
                .username("Skeptic123")
                .text("International expansion often faces challenges, hope they succeed.")
                .image(null)
                .time(Instant.parse("2025-08-14T12:40:00Z"))
                .vote(Vote.FAKE)
                .news(news10)
                .build();

        news10.getComments().add(c10_1);
        news10.getComments().add(c10_2);
        newsDao.save(news10);

        // --- ข่าวที่ 11: AI Novel Writing ---
        News news11 = News.builder()
                .topic("New AI Model Can Write Novels in Seconds")
                .shortDetail("Breakthrough AI model NovelGPT generates complete 300-page novels in under 30 seconds, sparking debate among publishers and authors about the future of creative writing.")
                .fullDetail("Researchers at Stanford University's AI Laboratory have unveiled NovelGPT, a revolutionary artificial intelligence model capable of generating complete, coherent novels of up to 300 pages in less than 30 seconds, representing a quantum leap in AI creative capabilities that has sent shockwaves through the publishing industry and literary community worldwide. The advanced neural network, trained on over 100,000 published novels across multiple genres, can produce original storylines, develop complex characters, and maintain narrative consistency throughout entire books, with early demonstrations showing the AI creating detective mysteries, romance novels, and science fiction epics that are virtually indistinguishable from human-written works. Publishing executives report receiving dozens of AI-generated manuscripts weekly, while major publishers like Penguin Random House have established new review protocols specifically for AI-authored content, raising unprecedented questions about authorship, creativity, and intellectual property rights in the digital age. Literary critic Dr. James Patterson expressed concerns that we're witnessing the potential democratization of novel writing, but also the possible devaluation of human creativity, while bestselling author Margaret Chen argued that AI can replicate patterns and structures, but it cannot capture the authentic human experience that makes great literature truly resonate with readers. The technology's creators estimate that NovelGPT could enable aspiring writers to produce first drafts instantly, revolutionize content creation for streaming platforms seeking original stories, and potentially flood the market with AI-generated literature, fundamentally altering how society defines and values human artistic expression in an increasingly automated world.")
                .image("https://miro.medium.com/v2/resize:fit:1200/1*4ETPeLePkGr0qjZrdsOB4Q.jpeg")
                .reporter("Emily Davis")
                .dateTime(Instant.parse("2025-08-01T10:00:00Z"))
                .realVotes(29)
                .fakeVotes(14)
                .build();

        Comment c11_1 = Comment.builder()
                .username("Ping")
                .text("AI can't replicate true human creativity.")
                .image("https://i.pinimg.com/736x/bf/6e/29/bf6e296386c67b027cd3d234e3c6efa4.jpg")
                .time(Instant.parse("2025-08-01T12:00:00Z"))
                .vote(Vote.FAKE)
                .news(news11)
                .build();

        Comment c11_2 = Comment.builder()
                .username("Jinhee Kwak")
                .text("Incredible technology! It could change the future of publishing, but I wonder how it will impact writers.")
                .image(null)
                .time(Instant.parse("2025-08-01T15:00:00Z"))
                .vote(Vote.REAL)
                .news(news11)
                .build();

        Comment c11_3 = Comment.builder()
                .username("Harper Harris")
                .text("This feels like a real turning point. The evidence makes it hard to doubt—AI really is moving faster than we ever imagined.")
                .image("https://i.pinimg.com/736x/aa/91/1a/aa911af80d78efb920dbbb37b8c94090.jpg")
                .time(Instant.parse("2025-08-01T15:00:00Z"))
                .vote(Vote.REAL)
                .news(news11)
                .build();

        news11.getComments().add(c11_1);
        news11.getComments().add(c11_2);
        news11.getComments().add(c11_3);
        newsDao.save(news11);

        // --- ข่าวที่ 12: Electric Bus Service (FAKE) ---
        News news12 = News.builder()
                .topic("City Introduces Free Electric Bus Service")
                .shortDetail("City launches revolutionary free electric bus network with 25 routes and 80 vehicles powered by experimental fusion technology, promising to eliminate all traffic pollution overnight.")
                .fullDetail("In a stunning announcement that has left transportation experts baffled, the city has launched what officials claim is the world's first fusion-powered electric bus fleet, featuring 80 vehicles equipped with miniaturized nuclear fusion reactors that allegedly produce zero emissions while generating unlimited clean energy. Mayor Sarah Thompson held a press conference surrounded by the gleaming white buses, declaring that this breakthrough technology, developed in secret partnership with undisclosed international scientists, will revolutionize urban transportation forever and make our city completely carbon-neutral within 30 days. The buses, which city officials insist can operate continuously for months without recharging, feature mysterious glowing panels and emit a faint humming sound that transportation engineers admit they cannot explain. Dr. Michael Rodriguez, a so-called quantum transportation specialist who appeared via video link from an undisclosed location, claimed that the fusion reactors are smaller than a briefcase yet more powerful than traditional power plants, while refusing to provide technical specifications citing national security concerns. Strangely, no major news outlets have been allowed to inspect the buses' interiors, and several independent scientists who requested access to examine the technology have been turned away by city security. Despite these unusual circumstances, ridership has reportedly reached an impossible 50,000 passengers daily on buses that appear to hold only 40 people each, leading some residents to question whether the entire program might be an elaborate publicity stunt designed to attract federal green energy funding.")
                .image("https://i0.wp.com/southeastasiainfra.com/wp-content/uploads/2023/08/ThaiSmile-buses.jpeg?resize=1200%2C640&ssl=1")
                .reporter("Emily Davis")
                .dateTime(Instant.parse("2025-08-02T18:00:00Z"))
                .realVotes(9)
                .fakeVotes(23)
                .build();

        Comment c12_1 = Comment.builder()
                .username("Lucie Bernard")
                .text("Experts weigh in on controversial topic.")
                .image(null)
                .time(Instant.parse("2025-08-02T19:00:00Z"))
                .vote(Vote.REAL)
                .news(news12)
                .build();

        Comment c12_2 = Comment.builder()
                .username("Elizabeth Garcia")
                .text("Fusion-powered buses? That sounds way too good to be true. The lack of verifiable details and scientific skepticism makes me think this is just an elaborate publicity stunt.")
                .image("https://i.pinimg.com/564x/d2/6f/ae/d26fae915e8781b2a16dc5a48673efac.jpg")
                .time(Instant.parse("2025-08-02T21:00:00Z"))
                .vote(Vote.FAKE)
                .news(news12)
                .build();

        Comment c12_3 = Comment.builder()
                .username("Thanayapat Ch.")
                .text("Fusion-powered buses? I really doubt this. If it were real, there would be far more scientific reports and technical details available.")
                .image("https://encrypted-tbn2.gstatic.com/images?q=tbn:ANd9GcQA9ykkfWX7n5Hrn0RYeoBa0mf7eErQvuTXM69inQaf_fwEYaei1WMsZ7pQg3sg")
                .time(Instant.parse("2025-08-02T22:00:00Z"))
                .vote(Vote.FAKE)
                .news(news12)
                .build();

        Comment c12_4 = Comment.builder()
                .username("Guo Shan")
                .text("Healing cancer with headphones in three days? Sorry, but that sounds more like science fiction than actual medicine.")
                .image("https://f.ptcdn.info/230/088/000/mbj2qtr0iMqzk96q3TF-o.jpg")
                .time(Instant.parse("2025-08-02T20:00:00Z"))
                .vote(Vote.FAKE)
                .news(news12)
                .build();

        news12.getComments().add(c12_1);
        news12.getComments().add(c12_2);
        news12.getComments().add(c12_3);
        news12.getComments().add(c12_4);
        newsDao.save(news12);

        // --- ข่าวที่ 13: TOI-715b Discovery ---
        News news13 = News.builder()
                .topic("Scientists Discover New Planet in Nearby Solar System")
                .shortDetail("Astronomers discover TOI-715b, a super-Earth exoplanet 137 light-years away in the habitable zone of its star, potentially capable of supporting liquid water and life.")
                .fullDetail("An international team of astronomers has announced the discovery of TOI-715b, a fascinating super-Earth exoplanet located 137 light-years away in the constellation Pictor, marking one of the most promising potentially habitable worlds found in recent years through data collected by NASA's Transiting Exoplanet Survey Satellite (TESS). The newly discovered planet, approximately 1.55 times the size of Earth, orbits within the habitable zone of its red dwarf star TOI-715, completing one orbit every 19.3 Earth days in conditions that could theoretically allow liquid water to exist on its surface. Lead researcher Dr. Georgina Dransfield from the University of Birmingham explained that TOI-715b represents an exciting target for follow-up observations with the James Webb Space Telescope, which could potentially detect atmospheric signatures that might indicate the presence of water vapor or even biosignatures. The discovery is particularly significant because red dwarf stars, which comprise about 75% of all stars in our galaxy, have extremely long lifespans that could provide stable conditions for billions of years, making them prime candidates for hosting life-sustaining planets. Additional observations have also revealed evidence of a second planet candidate in the same system, designated TOI-715c, which would be the smallest habitable-zone planet discovered by TESS if confirmed. Astrophysicist Dr. Emily Gilbert from the Jet Propulsion Laboratory noted that this system offers us a unique laboratory to study planetary formation and evolution around smaller stars, while emphasizing that future atmospheric studies will be crucial in determining whether these worlds could actually support life as we know it.")
                .image("https://assets.science.nasa.gov/dynamicimage/assets/science/astro/exo-explore/2023/09/k/kepler_all-planets_may20161280.jpg")
                .reporter("John Miller")
                .dateTime(Instant.parse("2025-08-03T20:00:00Z"))
                .realVotes(18)
                .fakeVotes(6)
                .build();

        Comment c13_1 = Comment.builder()
                .username("Abigail Thompson")
                .text("Feels too optimistic about habitability.")
                .image("https://i0.wp.com/www.gktoday.in/wp-content/uploads/2024/02/202402054101.png?w=328&ssl=1")
                .time(Instant.parse("2025-08-04T01:00:00Z"))
                .vote(Vote.FAKE)
                .news(news13)
                .build();

        Comment c13_2 = Comment.builder()
                .username("Pratchaya Srichok")
                .text("Until follow-up observations confirm, I won't trust it.")
                .image(null)
                .time(Instant.parse("2025-08-03T21:00:00Z"))
                .vote(Vote.FAKE)
                .news(news13)
                .build();

        news13.getComments().add(c13_1);
        news13.getComments().add(c13_2);
        newsDao.save(news13);

        // --- ข่าวที่ 14: Wearable Translator (FAKE) ---
        News news14 = News.builder()
                .topic("Startup Launches Wearable Translator Earbuds")
                .shortDetail("Tech startup claims revolutionary MindLink earbuds can translate any language instantly through direct brain wave interpretation, raising concerns among linguists and neuroscientists.")
                .fullDetail("Silicon Valley startup NeuroLingo has unveiled what it claims are the world's first telepathic translation earbuds called MindLink, which allegedly bypass traditional audio processing by directly reading users' brain waves to provide instant translation of any spoken language, including ancient dialects and even animal communication patterns. Company CEO Dr. Alexandra Chen, who holds what she describes as classified degrees in quantum linguistics, demonstrated the device at a secretive press event where reporters were not allowed to bring recording equipment, claiming the earbuds successfully translated conversations with dolphins, decoded 3,000-year-old Sanskrit texts, and even interpreted the emotional frequencies of houseplants. The earbuds, priced at an astounding $50,000 per pair, supposedly contain microscopic quantum processors that can synchronize with the universal language matrix, according to company materials that cite research from universities that fact-checkers have been unable to verify. Linguistics professor Dr. Maria Rodriguez from Stanford expressed skepticism, stating that the fundamental claims about brain-wave translation contradict everything we know about how language processing works, while neuroscientist Dr. James Park noted that no current technology can decode complex thoughts directly from brain activity with this level of precision. Despite these concerns, NeuroLingo reports receiving over 10,000 pre-orders from customers including unnamed government agencies and intergalactic communication researchers, though the company has refused to provide any independent testing data or allow scientific examination of their prototype devices, fueling speculation that the entire venture may be an elaborate cryptocurrency investment scheme.")
                .image("https://image.bastillepost.com/1200x/wp-content/uploads/global/2025/03/8419803_1741919148002_a_FB.jpg.webp")
                .reporter("Alice Johnson")
                .dateTime(Instant.parse("2025-08-04T10:00:00Z"))
                .realVotes(13)
                .fakeVotes(25)
                .build();

        Comment c14_1 = Comment.builder()
                .username("Wipada Boonyasang")
                .text("Could actually work with quantum tech.")
                .image("https://i.pinimg.com/736x/5a/ec/22/5aec2220c6ebe1869b059801e2107044.jpg")
                .time(Instant.parse("2025-08-04T11:00:00Z"))
                .vote(Vote.REAL)
                .news(news14)
                .build();

        Comment c14_2 = Comment.builder()
                .username("Pierre Dpt")
                .text("Brain-wave translation? Definitely skeptical.")
                .image(null)
                .time(Instant.parse("2025-08-04T14:00:00Z"))
                .vote(Vote.FAKE)
                .news(news14)
                .build();

        Comment c14_3 = Comment.builder()
                .username("Javier López")
                .text("Amazing tech, I can see this being possible.")
                .image(null)
                .time(Instant.parse("2025-08-04T15:00:00Z"))
                .vote(Vote.REAL)
                .news(news14)
                .build();

        news14.getComments().add(c14_1);
        news14.getComments().add(c14_2);
        news14.getComments().add(c14_3);
        newsDao.save(news14);

        // --- ข่าวที่ 15: Cancer Frequency Cure (FAKE) ---
        News news15 = News.builder()
                .topic("Breakthrough in Cancer Research Announced")
                .shortDetail("Controversial scientist claims miracle frequency sound waves can cure any cancer in 72 hours, sparking fierce debate among medical professionals and desperate patients worldwide.")
                .fullDetail("Dr. Marcus Heilmann, a self-proclaimed quantum oncologist operating from an undisclosed research facility, has announced what he calls the most significant medical breakthrough in human history - a revolutionary cancer treatment using specially calibrated sound frequencies that allegedly cure 100% of cancer cases within 72 hours without any side effects. According to Heilmann's unverified research, cancer cells vibrate at a specific death frequency of 528.7 Hz, and his proprietary HealWave technology can target and destroy malignant tumors by playing precisely tuned sound waves through modified headphones for just 20 minutes daily. The controversial treatment, which costs $75,000 per session and is only available at Heilmann's private clinic in an unnamed South American country, has reportedly attracted thousands of desperate patients despite fierce criticism from mainstream medical institutions. Dr. Sarah Johnson from the National Cancer Institute stated that there is absolutely no scientific evidence supporting frequency-based cancer treatment, and we're deeply concerned about patients abandoning proven therapies for unsubstantiated claims, while oncologist Dr. Michael Chen warned that this appears to be a dangerous exploitation of vulnerable cancer patients seeking miracle cures. Strangely, Heilmann claims to have cured over 5,000 patients but refuses to release medical records citing patient privacy laws, and several major medical journals have rejected his research papers for what peer reviewers describe as fundamental methodological flaws and impossible results. Despite mounting skepticism from the scientific community, testimonial videos featuring alleged cured patients continue circulating on social media, though investigations by health authorities have been unable to verify the identities or medical histories of any individuals featured in these promotional materials.")
                .image("https://d2ubrtwy6ww54e.cloudfront.net/www.uvmhealth.org/assets/styles/blog_hero/s3/2023-01/cancer-therapies-web.jpg")
                .reporter("Luis Rodríguez")
                .dateTime(Instant.parse("2025-08-05T15:00:00Z"))
                .realVotes(8)
                .fakeVotes(20)
                .build();

        newsDao.save(news15);

        // --- ข่าวที่ 16: Giant Sinkhole (FAKE) ---
        News news16 = News.builder()
                .topic("Giant Sinkhole Appears in Downtown Area")
                .shortDetail("Mysterious 500-foot deep sinkhole opens in city center, revealing ancient underground civilization with glowing crystals and hieroglyphic writings that archaeologists claim could rewrite human history.")
                .fullDetail("A massive sinkhole measuring 300 feet in diameter and an estimated 500 feet deep suddenly opened in the heart of downtown yesterday morning, swallowing three city blocks and exposing what city officials are calling an impossible archaeological discovery - the perfectly preserved remains of an advanced underground civilization complete with crystal-powered structures and walls covered in unidentified hieroglyphic symbols. Emergency response coordinator Captain James Mitchell, speaking from behind mysterious government-erected barriers that now surround the entire area, claimed that initial drone surveys revealed architectural elements that shouldn't exist according to our understanding of ancient technology, including what appears to be a functioning power grid made entirely of luminescent crystals that continue to emit a bright blue light. Dr. Elena Vasquez, a self-described xenoarchaeologist who was allegedly the first scientist lowered into the sinkhole, reported discovering tablets written in an unknown language that she believes may contain star maps pointing to Earth's original extraterrestrial visitors, though her findings have been immediately classified by federal authorities who arrived within hours of the discovery. Local geology professor Dr. Robert Kim expressed bewilderment, stating that geological surveys conducted just last month showed no underground cavities in this area, making this sinkhole's appearance scientifically impossible, while residents report strange electromagnetic interference affecting all electronic devices within a six-block radius since the hole appeared. Curiously, city officials have banned all media access to the site and are reportedly evacuating surrounding buildings under the pretense of structural safety concerns, leading conspiracy theorists to speculate that the government is covering up evidence of ancient alien contact rather than addressing what they claim is simply a routine geological phenomenon.")
                .image("https://media.wired.com/photos/59269d64cfe0d93c47430d6b/191:100/w_1280,c_limit/Before-623639136.jpg")
                .reporter("Sarah Lee")
                .dateTime(Instant.parse("2025-08-06T12:00:00Z"))
                .realVotes(13)
                .fakeVotes(14)
                .build();

        Comment c16_1 = Comment.builder()
                .username("Anna Suda")
                .text("This is absolutely mind-blowing! If the reports about glowing crystals and hieroglyphics are true, this could be the most important archaeological discovery in human history.")
                .image(null)
                .time(Instant.parse("2025-08-06T15:00:00Z"))
                .vote(Vote.REAL)
                .news(news16)
                .build();

        Comment c16_2 = Comment.builder()
                .username("Michael Prasit")
                .text("No geological evidence, no credible sources, and only mysterious 'classified findings.' Until there's hard proof, this is just another conspiracy theory.")
                .image("https://i.pinimg.com/1200x/86/bd/cc/86bdccac55661b840ac2050a3fe4c359.jpg")
                .time(Instant.parse("2025-08-06T17:00:00Z"))
                .vote(Vote.FAKE)
                .news(news16)
                .build();

        Comment c16_3 = Comment.builder()
                .username("Benjamin Miller")
                .text("I believe this. Governments always try to hide things like this, and the sudden media ban just makes it more convincing that they found something extraordinary underground.")
                .image("https://i.pinimg.com/736x/8e/47/ab/8e47abac1e9ac500a4ba17d9772408fd.jpg")
                .time(Instant.parse("2025-08-06T14:00:00Z"))
                .vote(Vote.REAL)
                .news(news16)
                .build();

        Comment c16_4 = Comment.builder()
                .username("Mia Anderson")
                .text("A 500-foot sinkhole revealing an 'ancient alien civilization' in the middle of a city? That sounds more like a sci-fi movie than real life.")
                .image(null)
                .time(Instant.parse("2025-08-06T16:00:00Z"))
                .vote(Vote.FAKE)
                .news(news16)
                .build();

        news16.getComments().add(c16_1);
        news16.getComments().add(c16_2);
        news16.getComments().add(c16_3);
        news16.getComments().add(c16_4);
        newsDao.save(news16);

        // --- ข่าวที่ 17: Tax Cut ---
        News news17 = News.builder()
                .topic("Government Announces Tax Cut for Small Businesses")
                .shortDetail("Government unveils comprehensive small business tax relief package reducing corporate rates from 25% to 18%, while critics question funding sources and long-term economic impact.")
                .fullDetail("The federal government announced a significant tax relief package for small businesses, reducing the corporate tax rate from 25% to 18% for companies with annual revenues under $2 million, a move that officials estimate will save qualifying businesses an average of $12,000 annually while potentially stimulating job creation and economic growth. Treasury Secretary Patricia Williams explained that the tax cuts, effective January 1st, are designed to help small enterprises recover from recent economic challenges and compete more effectively with larger corporations, though the initiative has sparked intense debate among economists and lawmakers about its fiscal implications. The proposal includes additional incentives such as expanded equipment depreciation allowances and simplified tax filing procedures, with the Small Business Administration projecting that approximately 850,000 businesses nationwide could benefit from the reduced rates. However, opposition leaders have raised concerns about the estimated $8.5 billion annual revenue loss to the federal budget, questioning how the government plans to offset the reduced income without cutting essential services or increasing the national deficit. Economic analyst Dr. Robert Chen from the Brookings Institution offered cautious optimism, noting that targeted tax relief can stimulate business investment and hiring, but the effectiveness depends largely on broader economic conditions and whether businesses actually use the savings for expansion rather than simply increasing profits. Meanwhile, small business owner Maria Rodriguez welcomed the news but expressed skepticism about the timing, stating that while tax cuts are always appreciated, many businesses are more concerned about supply chain disruptions and labor shortages that tax relief alone cannot address. The legislation still requires congressional approval, with heated debates expected over the coming weeks as lawmakers from both parties weigh the potential economic benefits against concerns about fiscal responsibility and equitable tax policy.")
                .image("https://assets.bwbx.io/images/users/iqjWHBFdfxIU/iNyEyqdV3pYY/v0/-1x-1.webp")
                .reporter("John Miller")
                .dateTime(Instant.parse("2025-08-07T16:00:00Z"))
                .realVotes(23)
                .fakeVotes(23)
                .build();

        Comment c17_1 = Comment.builder()
                .username("Golf")
                .text("This could be a real boost for small businesses. Cutting corporate taxes from 25% to 18% would definitely help struggling enterprises and stimulate the economy.")
                .image(null)
                .time(Instant.parse("2025-08-07T20:00:00Z"))
                .vote(Vote.REAL)
                .news(news17)
                .build();

        Comment c17_2 = Comment.builder()
                .username("Mila Sanchez")
                .text("I believe this is true. Governments often implement targeted tax relief programs, and the projected benefits for thousands of small businesses make sense.")
                .image(null)
                .time(Instant.parse("2025-08-07T21:00:00Z"))
                .vote(Vote.REAL)
                .news(news17)
                .build();

        Comment c17_3 = Comment.builder()
                .username("Stella Rogers")
                .text("An $8.5 billion budget hole? Sounds suspicious. Until Congress actually passes it and official documents are released, I'm skeptical this will happen.")
                .image("https://i.pinimg.com/736x/2c/5c/21/2c5c212e52ff6cd3d8b853758d54ba28.jpg")
                .time(Instant.parse("2025-08-07T20:00:00Z"))
                .vote(Vote.FAKE)
                .news(news17)
                .build();

        Comment c17_4 = Comment.builder()
                .username("Aum")
                .text("$8.5B lost? Seems fishy.")
                .image("https://i.pinimg.com/736x/29/e8/05/29e80518f21a781c1ddaf1479c298bf9.jpg")
                .time(Instant.parse("2025-08-07T19:00:00Z"))
                .vote(Vote.FAKE)
                .news(news17)
                .build();

        news17.getComments().add(c17_1);
        news17.getComments().add(c17_2);
        news17.getComments().add(c17_3);
        news17.getComments().add(c17_4);
        newsDao.save(news17);

        // --- ข่าวที่ 18: Holographic Phone ---
        News news18 = News.builder()
                .topic("New Smartphone Released with Holographic Display")
                .shortDetail("Samsung unveils Galaxy Holo featuring advanced light field technology that projects 3D holographic images up to 6 inches above the screen, priced at $2,800 for early adopters.")
                .fullDetail("Samsung has officially launched the Galaxy Holo, marking a significant milestone in mobile technology with the world's first commercially available smartphone featuring a functional holographic display system that uses advanced light field projection technology to create floating 3D images up to 6 inches above the device's surface. The breakthrough technology, developed in partnership with Israeli startup Looking Glass Factory, employs a proprietary array of micro-LEDs and precision optics to generate viewable holograms without requiring special glasses, though the effect is currently limited to a 45-degree viewing angle and works best in controlled lighting conditions. Samsung's head of mobile innovation, Dr. Lisa Park, demonstrated the device at Mobile World Congress, showing holographic video calls, 3D gaming experiences, and interactive product visualizations that respond to hand gestures detected by the phone's advanced depth sensors. The Galaxy Holo, priced at $2,800 for the 256GB model, represents a premium offering targeted at early technology adopters, content creators, and professionals in fields like architecture and medical imaging who could benefit from true 3D visualization capabilities. Technology analyst Ming-Chi Kuo noted that while the holographic display is genuinely impressive, current limitations including battery drain, viewing angle restrictions, and high production costs will likely keep this as a niche product until the technology matures. Early reviews from tech journalists who tested preview units report that the holographic effect works remarkably well for certain applications like 3D modeling and augmented reality experiences, though conventional smartphone tasks like reading text or browsing social media show little practical advantage over traditional displays. Samsung plans to release software development tools later this year to encourage app creators to explore holographic interfaces, while the company projects that refined versions of the technology could appear in mainstream devices within 3-5 years as manufacturing costs decrease and technical limitations are addressed.")
                .image("https://thedebrief.b-cdn.net/wp-content/uploads/2024/04/hologram.jpg")
                .reporter("Laura White")
                .dateTime(Instant.parse("2025-08-08T11:00:00Z"))
                .realVotes(29)
                .fakeVotes(11)
                .build();

        Comment c18_1 = Comment.builder()
                .username("Hannah")
                .text("Wow, a holographic phone? Looks legit.")
                .image("https://i.pinimg.com/736x/bb/71/23/bb7123aea24b79177c5a1602796fe70e.jpg")
                .time(Instant.parse("2025-08-08T12:00:00Z"))
                .vote(Vote.REAL)
                .news(news18)
                .build();

        Comment c18_2 = Comment.builder()
                .username("Aurora Collins")
                .text("This could actually be real tech, super cool!")
                .image(null)
                .time(Instant.parse("2025-08-08T13:00:00Z"))
                .vote(Vote.REAL)
                .news(news18)
                .build();

        Comment c18_3 = Comment.builder()
                .username("Kevin Young")
                .text("Exciting innovation! The price is steep, but if this tech is legit, it could set a new standard for mobile devices.")
                .image(null)
                .time(Instant.parse("2025-08-08T15:00:00Z"))
                .vote(Vote.REAL)
                .news(news18)
                .build();

        news18.getComments().add(c18_1);
        news18.getComments().add(c18_2);
        news18.getComments().add(c18_3);
        newsDao.save(news18);

        // --- ข่าวที่ 19: Renewable Energy Plan ---
        News news19 = News.builder()
                .topic("Government Launches New Renewable Energy Plan")
                .shortDetail("The government launches a $50 billion renewable energy plan aiming for 50% clean electricity by 2030, including major solar and wind projects, consumer incentives up to $15,000, and creating 250,000 green jobs to establish the nation as a global clean energy leader.")
                .fullDetail("The government has launched an ambitious $50 billion renewable energy initiative aimed at transforming the nation's power infrastructure, with the goal of generating 50% of electricity from renewable sources by 2030. This comprehensive plan includes the construction of 15 major solar farms and 20 offshore wind installations, alongside advanced energy storage systems and a $12 billion smart grid modernization program to enhance efficiency and reliability. Citizens will receive substantial benefits including rebates up to $15,000 for solar panel installations, zero-interest loans for energy upgrades, and $8,000 credits for electric vehicle purchases, while businesses can access enhanced tax incentives for clean energy investments. The initiative is projected to create 250,000 new jobs across engineering, manufacturing, and installation sectors, reduce household energy costs by 30%, and decrease national carbon emissions by 40% within five years. Energy Minister Sarah Chen emphasized that the plan represents more than climate action, stating it will secure energy independence and position the country as a global leader in clean energy technology, with quarterly progress reports ensuring full transparency and accountability throughout the implementation process.")
                .image("https://media.nationthailand.com/uploads/images/md/2024/06/wfnb7E2ihaF0IGs0Vk53.webp")
                .reporter("Michael Lee")
                .dateTime(Instant.parse("2025-08-14T16:27:45.406629Z"))
                .realVotes(150)
                .fakeVotes(150)
                .build();

        Comment c19_1 = Comment.builder()
                .username("John Doe")
                .text("Great step forward! 🌱 Very promising for the economy and environment.")
                .image("https://images.unsplash.com/photo-1654083198752-56ff209c8129?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1000&q=80")
                .time(Instant.parse("2025-08-14T10:30:00Z"))
                .vote(Vote.REAL)
                .news(news19)
                .build();

        Comment c19_2 = Comment.builder()
                .username("Jane Smith")
                .text("This could be a game changer for the environment. If they follow through, this plan would not only help with clean energy but also create thousands of jobs.")
                .image(null)
                .time(Instant.parse("2025-08-14T11:45:00Z"))
                .vote(Vote.REAL)
                .news(news19)
                .build();

        Comment c19_3 = Comment.builder()
                .username("Fake News Watcher")
                .text("I'm not buying it. A 50% renewable target by 2030 sounds too ambitious, especially with how slow the implementation has been in the past.")
                .image(null)
                .time(Instant.parse("2025-08-14T12:00:00Z"))
                .vote(Vote.FAKE)
                .news(news19)
                .build();

        news19.getComments().add(c19_1);
        news19.getComments().add(c19_2);
        news19.getComments().add(c19_3);
        newsDao.save(news19);

        // --- ข่าวที่ 20: Mystery Illness ---
        News news20 = News.builder()
                .topic("Mystery Illness Spreads in Rural Town")
                .shortDetail("Over 200 residents in Millbrook have contracted an unknown respiratory illness, prompting CDC investigation into the mysterious pathogen causing fever and persistent cough.")
                .fullDetail("The quiet farming community of Millbrook, population 3,500, has become the center of an intensive medical investigation after more than 200 residents developed a mysterious respiratory illness characterized by severe cough, high fever, and prolonged fatigue that doesn't match any known disease patterns. The outbreak began three weeks ago when several patients visited the local clinic with similar symptoms that failed to respond to standard treatments for flu, COVID-19, or bacterial infections. Dr. Elizabeth Warren, the town's chief medical officer, immediately contacted state health authorities when preliminary tests came back negative for all common respiratory pathogens. The Centers for Disease Control and Prevention has since deployed a rapid response team led by epidemiologist Dr. Michael Rodriguez, who established a temporary command center at Millbrook High School to coordinate the investigation and provide additional medical support. Environmental health specialists are examining potential sources including the town's water supply, a nearby agricultural processing plant, and recent changes in local farming practices, while virologists at the CDC's Atlanta laboratory are analyzing blood and tissue samples using advanced genetic sequencing techniques. Mayor Sarah Thompson has implemented voluntary isolation protocols and canceled all public gatherings as a precautionary measure, while assuring residents that food and medical supplies continue to arrive daily. The illness appears to have a recovery rate of 95% with most patients showing improvement after 10-14 days of supportive care, though health officials emphasize the importance of identifying the cause to prevent further spread to neighboring communities.")
                .image("https://i.guim.co.uk/img/media/ac19dc69e4510d4f2306f1450a097c5d95ea7a26/0_109_2500_1499/master/2500.jpg?width=1200&quality=85&auto=format&fit=max&s=3a5cdfed87e118dcdf0cd2ae924f99fd")
                .reporter("John Miller")
                .dateTime(Instant.parse("2025-08-10T16:00:00Z"))
                .realVotes(8)
                .fakeVotes(14)
                .build();

        Comment c20_1 = Comment.builder()
                .username("Penelope Gonzalez")
                .text("Hope the CDC finds the cause soon—this sounds serious.")
                .image(null)
                .time(Instant.parse("2025-08-10T21:00:00Z"))
                .vote(Vote.REAL)
                .news(news20)
                .build();

        Comment c20_2 = Comment.builder()
                .username("Nok")
                .text("200 people sick? Sounds exaggerated.")
                .image("https://i.pinimg.com/564x/03/9f/98/039f988ed60a27700294098950ff457f.jpg")
                .time(Instant.parse("2025-08-10T21:00:00Z"))
                .vote(Vote.FAKE)
                .news(news20)
                .build();

        Comment c20_3 = Comment.builder()
                .username("Plaa")
                .text("Could be panic or misinformation, I'm skeptical.")
                .image(null)
                .time(Instant.parse("2025-08-10T19:00:00Z"))
                .vote(Vote.FAKE)
                .news(news20)
                .build();

        Comment c20_4 = Comment.builder()
                .username("Liu Qiang")
                .text("Until CDC releases official statement, I won't believe it😡")
                .image(null)
                .time(Instant.parse("2025-08-10T21:00:00Z"))
                .vote(Vote.FAKE)
                .news(news20)
                .build();

        Comment c20_5 = Comment.builder()
                .username("Jay")
                .text("Could just be flu misreported, not really unknown.")
                .image(null)
                .time(Instant.parse("2025-08-10T20:00:00Z"))
                .vote(Vote.FAKE)
                .news(news20)
                .build();

        news20.getComments().add(c20_1);
        news20.getComments().add(c20_2);
        news20.getComments().add(c20_3);
        news20.getComments().add(c20_4);
        news20.getComments().add(c20_5);
        newsDao.save(news20);

        // --- ข่าวที่ 21: Solar Farm ---
        News news21 = News.builder()
                .topic("World's Largest Solar Farm Opens")
                .shortDetail("The Al Dhafra Solar Farm in Abu Dhabi started operations, generating 2 gigawatts of clean energy to power 200,000 homes, marking a key milestone in renewable energy.")
                .fullDetail("The Al Dhafra Solar Farm in Abu Dhabi has officially commenced operations as the world's largest single-site solar installation, spanning 20 square kilometers and generating 2 gigawatts of clean electricity capable of powering approximately 200,000 homes while reducing carbon emissions by 2.4 million tons annually. The $871 million project, developed through a partnership between Emirates Water and Electricity Company, Masdar, EDF Renewables, and Jinko Power, features over 4 million high-efficiency bifacial solar panels that utilize both direct sunlight and reflected light from the ground to maximize energy production. Crown Prince of Abu Dhabi Sheikh Khaled bin Mohamed bin Zayed Al Nahyan inaugurated the facility yesterday, emphasizing the UAE's commitment to achieving net-zero emissions by 2050 and positioning the nation as a global leader in renewable energy technology. The project employed over 4,000 workers during its three-year construction phase and represents a significant step toward the UAE's goal of generating 50% of its electricity from clean sources by 2050. International energy analysts predict that the farm's success will accelerate similar mega-projects worldwide, with the International Renewable Energy Agency noting that such large-scale installations are crucial for achieving global climate targets. The facility incorporates advanced tracking systems that follow the sun's movement throughout the day, increasing energy output by up to 20% compared to fixed installations, while also serving as a testing ground for next-generation photovoltaic technologies that could further revolutionize the solar industry.")
                .image("https://agreenerlifeagreenerworld.net/wp-content/uploads/2024/07/tarim-basin-solar-farm.-photo-credit-cfp.jpeg")
                .reporter("Alice Johnson")
                .dateTime(Instant.parse("2025-08-11T15:00:00Z"))
                .realVotes(13)
                .fakeVotes(3)
                .build();

        Comment c21_1 = Comment.builder()
                .username("Nokky")
                .text("Makes sense with all the solar tech advances.")
                .image(null)
                .time(Instant.parse("2025-08-11T16:00:00Z"))
                .vote(Vote.REAL)
                .news(news21)
                .build();

        Comment c21_2 = Comment.builder()
                .username("Joy")
                .text("Too perfect to be true, need proof.")
                .image("https://i.pinimg.com/736x/34/64/ad/3464ad1c33c983b87d66f14b092f11ee.jpg")
                .time(Instant.parse("2025-08-11T18:00:00Z"))
                .vote(Vote.FAKE)
                .news(news21)
                .build();

        news21.getComments().add(c21_1);
        news21.getComments().add(c21_2);
        newsDao.save(news21);

        // --- ข่าวที่ 22: Lab-Grown Meat ---
        News news22 = News.builder()
                .topic("Researchers Develop Lab-Grown Meat at Scale")
                .shortDetail("Scientists at Netherlands-based Mosa Meat have achieved commercial-scale production of lab-grown beef, reducing costs by 90% and paving the way for widespread availability of cultivated meat in supermarkets by 2026.")
                .fullDetail("Breakthrough research by Mosa Meat, the Netherlands-based biotechnology company founded by Dr. Mark Post, has successfully scaled up lab-grown beef production to industrial levels, achieving a dramatic 90% cost reduction that brings cultivated meat closer to price parity with conventional beef for the first time in the industry's history. The company's new 50,000-square-foot facility in Maastricht can produce 10,000 pounds of cultivated beef monthly using advanced bioreactor technology that grows animal cells in nutrient-rich media without the need for animal slaughter. Dr. Post, who created the world's first lab-grown burger in 2013, announced that production costs have dropped from $330,000 per pound to under $50 per pound through optimized cell lines, improved growth media formulations, and automated manufacturing processes. The European Food Safety Authority granted preliminary approval for the product last month, with full commercial authorization expected by early 2025, while the company has secured partnerships with major European retailers including Albert Heijn and Carrefour for distribution across 15 countries. Environmental scientists praise the development, noting that cultivated meat production requires 96% less land, 82% less water, and generates 78% fewer greenhouse gas emissions compared to conventional beef farming. Industry analysts project that the global cultivated meat market could reach $25 billion by 2030, with competing companies like Upside Foods and Good Meat racing to achieve similar production milestones as consumer acceptance grows and regulatory frameworks evolve worldwide.")
                .image("https://cdn.mos.cms.futurecdn.net/ZTP8d2zAcm5Yjz2VjNZJBM-1200-80.jpg")
                .reporter("John Miller")
                .dateTime(Instant.parse("2025-08-12T20:00:00Z"))
                .realVotes(19)
                .fakeVotes(19)
                .build();

        Comment c22_1 = Comment.builder()
                .username("Oil")
                .text("I'd like to see real supermarket availability first.")
                .image("https://i.pinimg.com/1200x/43/76/5a/43765acb57f02cb60b92d22c53ee1401.jpg")
                .time(Instant.parse("2025-08-13T00:00:00Z"))
                .vote(Vote.FAKE)
                .news(news22)
                .build();

        Comment c22_2 = Comment.builder()
                .username("Jaruwan Boonmak")
                .text("Could be marketing spin, not reality yet.")
                .image("https://i.pinimg.com/736x/3a/5c/91/3a5c9122e645b7c6b7f7f335779ee89e.jpg")
                .time(Instant.parse("2025-08-12T21:00:00Z"))
                .vote(Vote.FAKE)
                .news(news22)
                .build();

        Comment c22_3 = Comment.builder()
                .username("Ethan White")
                .text("Too perfect to be true, need verification.")
                .image("https://i.pinimg.com/736x/1b/96/c5/1b96c54cbd0339b11a7da86d65f06a6e.jpg")
                .time(Instant.parse("2025-08-13T00:00:00Z"))
                .vote(Vote.FAKE)
                .news(news22)
                .build();

        Comment c22_4 = Comment.builder()
                .username("Worawit Promraksa")
                .text("If experts say it's real, I trust this.")
                .image(null)
                .time(Instant.parse("2025-08-12T23:00:00Z"))
                .vote(Vote.REAL)
                .news(news22)
                .build();

        Comment c22_5 = Comment.builder()
                .username("Kim Ji-hyun")
                .text("This could really change the way we eat.")
                .image(null)
                .time(Instant.parse("2025-08-12T21:00:00Z"))
                .vote(Vote.REAL)
                .news(news22)
                .build();

        news22.getComments().add(c22_1);
        news22.getComments().add(c22_2);
        news22.getComments().add(c22_3);
        news22.getComments().add(c22_4);
        news22.getComments().add(c22_5);
        newsDao.save(news22);

        // --- ข่าวที่ 23: Data Breach ---
        News news23 = News.builder()
                .topic("Major Data Breach Affects Millions of Users")
                .shortDetail("ConnectWorld revealed that hackers breached the personal data of 45 million users, including passwords and financial info, leading to security updates and a government investigation.")
                .fullDetail("ConnectWorld, the popular social networking platform with over 200 million active users, announced yesterday that cybercriminals successfully breached their security systems and accessed sensitive personal information of approximately 45 million users between March 15-22, including usernames, email addresses, encrypted passwords, phone numbers, and stored payment card details for premium subscribers. The company's Chief Security Officer, Dr. Jennifer Martinez, revealed during an emergency press conference that the attack was discovered by their automated threat detection system on March 23, when unusual data transfer patterns triggered security alerts, leading to the immediate involvement of cybersecurity firm CyberShield and federal law enforcement agencies. The sophisticated breach appears to have exploited a previously unknown vulnerability in the platform's third-party authentication system, which hackers used to gain elevated access privileges and extract user data over a seven-day period before detection. ConnectWorld CEO Michael Thompson issued a formal apology and announced that all affected users have been automatically logged out and must reset their passwords, while the company has implemented additional multi-factor authentication requirements and upgraded their encryption protocols. The FBI's Cyber Crime Division, led by Special Agent Sarah Collins, has launched a full investigation and believes the attack may be linked to the international hacking group known as 'DataVault,' which has previously targeted major technology companies for financial gain. Cybersecurity experts warn that affected users should immediately change passwords on other accounts that may share similar credentials, monitor their financial statements for unauthorized transactions, and remain vigilant for phishing attempts as stolen data often appears on dark web marketplaces within weeks of major breaches.")
                .image("https://www.secpod.com/blog/wp-content/uploads/2020/06/data-breach.jpg")
                .reporter("Sophia Martinez")
                .dateTime(Instant.parse("2025-08-13T14:00:00Z"))
                .realVotes(19)
                .fakeVotes(29)
                .build();

        newsDao.save(news23);

        // --- ข่าวที่ 24: Drone Delivery (FAKE) ---
        News news24 = News.builder()
                .topic("Drone Delivery Service Expands Nationwide")
                .shortDetail("SkyDelivery announced plans to launch drone delivery services in 50 major cities by year-end, sparking debate among aviation experts and local officials about airspace safety and regulatory oversight.")
                .fullDetail("SkyDelivery, the innovative logistics company backed by venture capital firm TechVentures, unveiled ambitious plans yesterday to expand their autonomous drone delivery network to 50 major metropolitan areas across the United States by December 2025, representing the largest civilian drone operation in aviation history and igniting fierce debate among industry experts, federal regulators, and municipal authorities. CEO David Park announced that the company's fleet of 10,000 advanced hexacopter drones will provide 30-minute delivery of packages weighing up to 5 pounds within a 15-mile radius of designated distribution centers, utilizing artificial intelligence navigation systems and redundant safety protocols developed in partnership with aerospace manufacturer AeroDyne Technologies. The Federal Aviation Administration has granted conditional approval for the expansion under their new Commercial Drone Integration Program, though aviation safety expert Dr. Linda Harrison from the National Transportation Safety Board expressed concerns about potential mid-air collisions and the strain on already congested urban airspace. Local government officials remain divided on the initiative, with Seattle Mayor James Rodriguez praising the environmental benefits and job creation potential, while Phoenix City Council member Maria Santos cited noise pollution and privacy invasion as primary concerns requiring additional public hearings. The service promises to revolutionize e-commerce delivery for major retailers including QuickMart and GlobalShop, though critics argue that insufficient testing of the AI guidance systems in adverse weather conditions could pose significant risks to public safety. Transportation policy analyst Professor Robert Chen from Stanford University predicts that successful implementation could trigger a $50 billion transformation of the logistics industry within five years, while also necessitating comprehensive updates to federal aviation regulations and local zoning ordinances to accommodate the new aerial delivery infrastructure.")
                .image("https://s24806.pcdn.co/wp-content/uploads/2025/06/doordash-flytrex-delivery-970.jpg")
                .reporter("Alice Johnson")
                .dateTime(Instant.parse("2025-08-14T13:00:00Z"))
                .realVotes(3)
                .fakeVotes(21)
                .build();

        Comment c24_1 = Comment.builder()
                .username("Maxie")
                .text("Too ambitious, I'm skeptical.")
                .image("https://i.pinimg.com/1200x/24/17/24/241724215dde007280e57ec42e43793e.jpg")
                .time(Instant.parse("2025-08-14T18:00:00Z"))
                .vote(Vote.FAKE)
                .news(news24)
                .build();

        Comment c24_2 = Comment.builder()
                .username("Jiraporn Boonying")
                .text("Urban airspace is too crowded for this.")
                .image(null)
                .time(Instant.parse("2025-08-14T15:00:00Z"))
                .vote(Vote.FAKE)
                .news(news24)
                .build();

        Comment c24_3 = Comment.builder()
                .username("Aronong Prasertsuk")
                .text("Officials provide updates on the situation.")
                .image("https://i.pinimg.com/736x/f5/13/c1/f513c1879d645f57174e88034f5692a7.jpg")
                .time(Instant.parse("2025-08-14T17:00:00Z"))
                .vote(Vote.REAL)
                .news(news24)
                .build();

        Comment c24_4 = Comment.builder()
                .username("Toon")
                .text("Makes sense with current drone technology.")
                .image("https://i.pinimg.com/1200x/11/10/6c/11106ccb869afcab5bba203a23a4d896.jpg")
                .time(Instant.parse("2025-08-14T16:00:00Z"))
                .vote(Vote.REAL)
                .news(news24)
                .build();

        Comment c24_5 = Comment.builder()
                .username("Joshua Nelson")
                .text("If this works as promised, it could revolutionize the delivery industry. The potential for fast and efficient deliveries is huge, but airspace safety will be key.")
                .image(null)
                .time(Instant.parse("2025-08-14T17:00:00Z"))
                .vote(Vote.REAL)
                .news(news24)
                .build();

        news24.getComments().add(c24_1);
        news24.getComments().add(c24_2);
        news24.getComments().add(c24_3);
        news24.getComments().add(c24_4);
        news24.getComments().add(c24_5);
        newsDao.save(news24);

        // --- ข่าวที่ 25: Heatwave ---
        News news25 = News.builder()
                .topic("Unprecedented Heatwave Hits Northern Europe")
                .shortDetail("Northern Europe faces record temperatures of 45°C, causing power outages and emergency cooling centers as residents struggle with the extreme heat.")
                .fullDetail("An extraordinary heatwave has engulfed Northern Europe for the past week, with temperatures soaring to unprecedented levels of 47°C in Stockholm, 45°C in Helsinki, and 44°C in Copenhagen, shattering century-old weather records and forcing governments across the region to declare national heat emergencies. The extreme weather event, attributed by meteorologists to a persistent high-pressure system combined with climate change effects, has overwhelmed electrical grids as air conditioning usage peaked, causing rolling blackouts in major cities and prompting utility companies to implement emergency power rationing measures. Swedish Prime Minister Erik Lundberg announced the opening of 200 emergency cooling centers in schools and community buildings, while Norwegian authorities have distributed over 500,000 free fans to elderly residents most vulnerable to heat-related illness. Local resident Maria Johansson from Stockholm expressed shock at the conditions, stating that her apartment reached 38°C despite closed blinds and fans, while Helsinki resident Pekka Virtanen reported that his usual morning jog became impossible due to the oppressive heat. Climate scientist Dr. Anna Bergström from the University of Copenhagen warned that this heatwave represents a dangerous preview of future summer conditions in the region, with computer models suggesting similar events could become annual occurrences by 2035. Emergency medical services have reported a 300% increase in heat-related hospitalizations, particularly among the elderly and those with pre-existing conditions, while agricultural experts estimate that the extreme temperatures have damaged 40% of regional crop yields, potentially leading to food shortages. The European Centre for Medium-Range Weather Forecasts predicts the heatwave will persist for at least another ten days, prompting calls from environmental activists for immediate action on carbon emissions and infrastructure adaptation to handle increasingly severe weather patterns.")
                .image("https://www.ecowatch.com/wp-content/uploads/2021/10/1706207636-origin.jpg")
                .reporter("David Brown")
                .dateTime(Instant.parse("2025-08-15T16:00:00Z"))
                .realVotes(19)
                .fakeVotes(23)
                .build();

        Comment c25_1 = Comment.builder()
                .username("Joseph Moore")
                .text("This sounds like media fear-mongering. Sure, it's hot, but I'm not buying the idea that it's the worst heatwave ever. They always say that.")
                .image(null)
                .time(Instant.parse("2025-08-15T19:00:00Z"))
                .vote(Vote.FAKE)
                .news(news25)
                .build();

        Comment c25_2 = Comment.builder()
                .username("Felix K.J.")
                .text("This is terrifying. With temperatures hitting these levels, it's clear that climate change is having a serious impact. We need to take action before it's too late.")
                .image("https://i.pinimg.com/736x/56/15/20/561520db0c3626e181b5768426efe263.jpg")
                .time(Instant.parse("2025-08-15T19:00:00Z"))
                .vote(Vote.REAL)
                .news(news25)
                .build();

        Comment c25_3 = Comment.builder()
                .username("Stella Carpenter")
                .text("A heatwave this extreme in Northern Europe? Seems like another exaggeration. How could they not have prepared for something like this?")
                .image(null)
                .time(Instant.parse("2025-08-15T18:00:00Z"))
                .vote(Vote.FAKE)
                .news(news25)
                .build();

        news25.getComments().add(c25_1);
        news25.getComments().add(c25_2);
        news25.getComments().add(c25_3);
        newsDao.save(news25);

        // --- ข่าวที่ 26: Battery Technology ---
        News news26 = News.builder()
                .topic("Breakthrough Battery Technology Promises 5-Minute Charge")
                .shortDetail("TechPower Labs unveiled solid-state batteries that charge electric vehicles in 5 minutes and last 1 million miles, potentially transforming the automotive industry.")
                .fullDetail("TechPower Labs announced a revolutionary breakthrough in solid-state battery technology that enables electric vehicles to achieve full charge in just 5 minutes while maintaining battery life for over 1 million miles of driving. The new PowerCell-X batteries utilize proprietary silicon nanowire anodes and ceramic electrolytes developed by Dr. Rachel Kim's team, achieving energy density 300% higher than current lithium-ion batteries while eliminating fire risks. CEO Mark Stevens demonstrated the technology by charging a Tesla Model S equivalent battery pack from 0% to 100% in 4 minutes and 47 seconds using their prototype ultra-fast charging station. Major automakers including Ford and General Motors have expressed significant interest, with Ford signing a preliminary $12 billion supply agreement for integration into their 2027 vehicle lineup. However, industry analyst Dr. Jennifer Martinez cautioned that mass production challenges and charging infrastructure costs could delay widespread implementation by 3-5 years. Energy Secretary Amanda Roberts praised the innovation as crucial for achieving 50% EV market share by 2030, while TechPower Labs plans to begin pilot production next year with initial capacity of 100,000 battery packs annually.")
                .image("https://carnewschina.com/wp-content/uploads/2025/04/768d4066b04f4dc7bc58c3e504f05f4d.png")
                .reporter("Zhang Wei")
                .dateTime(Instant.parse("2025-08-16T08:00:00Z"))
                .realVotes(2)
                .fakeVotes(5)
                .build();

        Comment c26_1 = Comment.builder()
                .username("Harper Turner")
                .text("I'm skeptical. This is probably just another overhyped announcement. Even if it's true, it will take years before it's available for everyday use.")
                .image("https://i.pinimg.com/736x/ec/ec/d1/ececd1a7e07d5dce6eeadd66d3b6faeb.jpg")
                .time(Instant.parse("2025-08-16T13:00:00Z"))
                .vote(Vote.FAKE)
                .news(news26)
                .build();

        Comment c26_2 = Comment.builder()
                .username("Oliver Harris")
                .text("This technology sounds promising. If it lives up to its claims, it could really transform how we think about electric cars and energy storage.")
                .image("https://image.made-in-china.com/251f0j00UYfGuGRPdEVh/made-in-china.jpg")
                .time(Instant.parse("2025-08-16T11:00:00Z"))
                .vote(Vote.REAL)
                .news(news26)
                .build();

        news26.getComments().add(c26_1);
        news26.getComments().add(c26_2);
        newsDao.save(news26);

        // --- ข่าวที่ 27: Landmark Restoration ---
        News news27 = News.builder()
                .topic("Famous Landmark to Undergo Restoration")
                .shortDetail("The Statue of Liberty faces a controversial $500 million restoration using modern materials, sparking debate about preserving authenticity versus structural integrity.")
                .fullDetail("The National Park Service announced a comprehensive $500 million restoration of the Statue of Liberty, involving replacement of deteriorating sections with advanced composite materials to combat climate change effects, sparking debate among preservation experts about historical authenticity versus structural stability. The five-year project, led by restoration architect Dr. Maria Gonzalez, will replace the statue's corroding iron framework with lightweight titanium alloy supports while maintaining the copper exterior through advanced patina-matching techniques. Park Service Director James Mitchell defended the approach, citing engineering reports showing the 139-year-old landmark faces structural failure within a decade due to rising sea levels, storms, and pollution. Art historian Professor Catherine Wells from Columbia University criticized the plan, arguing that replacing original materials fundamentally alters historical integrity and sets dangerous precedents for cultural artifacts. The French government has requested involvement through their Ministry of Culture, expressing concerns about maintaining symbolic authenticity while acknowledging preservation necessity. Tourism representatives estimate extended closure could cost New York City over $2 billion in lost revenue, while environmental groups praise climate adaptation features. Public polls show Americans evenly divided, with 48% supporting modern preservation techniques and 47% preferring traditional methods despite potentially reduced effectiveness against environmental threats.")
                .image("https://www.telegraph.co.uk/content/dam/Travel/2018/June/sphinx-GettyImages-643614006.jpg?imwidth=640")
                .reporter("Emily Davis")
                .dateTime(Instant.parse("2025-08-17T14:00:00Z"))
                .realVotes(5)
                .fakeVotes(15)
                .build();

        Comment c27_1 = Comment.builder()
                .username("Li Ting")
                .text("How convenient! Replacing the statue's original materials with modern ones? Feels like they're ruining history in the name of 'preservation.")
                .image(null)
                .time(Instant.parse("2025-08-17T15:00:00Z"))
                .vote(Vote.FAKE)
                .news(news27)
                .build();

        Comment c27_2 = Comment.builder()
                .username("Paulo Santos")
                .text("I find it hard to believe that a statue that's lasted for 139 years suddenly needs such drastic changes. This sounds like an excuse for corporate interests.")
                .image(null)
                .time(Instant.parse("2025-08-17T19:00:00Z"))
                .vote(Vote.FAKE)
                .news(news27)
                .build();

        Comment c27_3 = Comment.builder()
                .username("Suwanna n.")
                .text("I'm all for preserving history, but replacing original parts with new materials feels like changing what made the statue iconic. What's next, a complete overhaul?")
                .image(null)
                .time(Instant.parse("2025-08-17T15:00:00Z"))
                .vote(Vote.FAKE)
                .news(news27)
                .build();

        Comment c27_4 = Comment.builder()
                .username("João Silva")
                .text("This project sounds like a disaster. If it's not broken, why fix it? The Statue of Liberty has stood the test of time without all these modern fixes.")
                .image("https://i.pinimg.com/736x/18/f4/70/18f470dcba6c8eff8326060bc6215c50.jpg")
                .time(Instant.parse("2025-08-17T15:00:00Z"))
                .vote(Vote.FAKE)
                .news(news27)
                .build();

        news27.getComments().add(c27_1);
        news27.getComments().add(c27_2);
        news27.getComments().add(c27_3);
        news27.getComments().add(c27_4);
        newsDao.save(news27);

        // --- ข่าวที่ 28: Ancient City ---
        News news28 = News.builder()
                .topic("Archaeologists Unearth Ancient City")
                .shortDetail("Archaeologists discovered a 4,000-year-old Mesopotamian city in Iraq with advanced infrastructure and cuneiform tablets that could rewrite ancient history.")
                .fullDetail("An international team led by Dr. Sarah Johnson from Oxford University has uncovered a remarkably preserved 4,000-year-old Mesopotamian city near Baghdad, featuring sophisticated urban infrastructure including paved roads, drainage systems, and multi-story buildings that challenge assumptions about Bronze Age capabilities. The 300-acre site contains over 15,000 cuneiform tablets providing unprecedented insights into ancient trade networks, legal systems, and daily life during the Babylonian Empire. Initial translations by Professor Michael Chen reveal previously unknown trade routes connecting Mesopotamia to the Indus Valley, suggesting more extensive ancient global commerce than historians believed. Extraordinary artifacts include intricately carved cylinder seals, advanced bronze weapons, and ceramics with astronomical observations demonstrating sophisticated mathematical knowledge. Iraqi Minister of Culture Dr. Layla Al-Rashid called it potentially the most significant regional archaeological find since the 1920s, while UNESCO announced emergency funding for site protection. The research team, collaborating with Iraqi antiquities authorities, plans a permanent research station with digital preservation technology to create 3D models of structures and artifacts. Preliminary analysis suggests the city was suddenly abandoned around 1800 BCE, possibly due to climate change or invasion, with residents leaving behind valuable possessions and documents, making this a unique time capsule of ancient Mesopotamian civilization.")
                .image("https://idsb.tmgrup.com.tr/ly/uploads/images/2021/04/10/106838.jpg")
                .reporter("Yusuf Ibrahim")
                .dateTime(Instant.parse("2025-08-18T17:00:00Z"))
                .realVotes(27)
                .fakeVotes(11)
                .build();

        Comment c28_1 = Comment.builder()
                .username("Grace Williams")
                .text("Incredible! I've always thought there's so much more to ancient civilizations than what we've been taught. This find proves it.")
                .image(null)
                .time(Instant.parse("2025-08-18T18:00:00Z"))
                .vote(Vote.REAL)
                .news(news28)
                .build();

        Comment c28_2 = Comment.builder()
                .username("Daniel T.")
                .text("This sounds like a publicity stunt. How could they have missed such an important site for so long?")
                .image(null)
                .time(Instant.parse("2025-08-18T20:00:00Z"))
                .vote(Vote.FAKE)
                .news(news28)
                .build();

        Comment c28_3 = Comment.builder()
                .username("Mason Lewis")
                .text("I'm not buying this. Every time they 'unearth' something amazing, it ends up being exaggerated or debunked later.")
                .image(null)
                .time(Instant.parse("2025-08-18T18:00:00Z"))
                .vote(Vote.FAKE)
                .news(news28)
                .build();

        news28.getComments().add(c28_1);
        news28.getComments().add(c28_2);
        news28.getComments().add(c28_3);
        newsDao.save(news28);

        // --- ข่าวที่ 29: Self-Driving Taxis ---
        News news29 = News.builder()
                .topic("Self-Driving Taxis Begin Service in Capital")
                .shortDetail("Autonomous taxi service RoboRide launched in Washington D.C. with 200 driverless vehicles, marking the first fully automated taxi fleet in a major U.S. capital city.")
                .fullDetail("RoboRide, the San Francisco-based autonomous vehicle company, officially launched the first fully driverless taxi service in Washington D.C. yesterday with a fleet of 200 electric vehicles operating 24/7 across downtown and surrounding neighborhoods, representing a historic milestone in urban transportation and sparking both excitement and safety concerns among residents and officials. The sleek white vehicles, equipped with advanced LiDAR sensors, cameras, and AI navigation systems developed over eight years of testing, can be summoned through a smartphone app and operate without human safety drivers for the first time in the nation's capital. D.C. Mayor Patricia Williams celebrated the launch at a ribbon-cutting ceremony, emphasizing the potential for reduced traffic congestion and improved mobility for disabled residents, while acknowledging ongoing discussions with labor unions representing traditional taxi drivers who fear job displacement. The service initially covers a 25-square-mile area including Capitol Hill, downtown, and Georgetown, with plans to expand citywide by 2026 pending regulatory approval and public acceptance. However, transportation safety advocate Dr. Robert Martinez from the Highway Safety Institute expressed concerns about the vehicles' ability to handle unpredictable situations like construction zones and emergency vehicles, citing recent incidents in other test cities. Federal regulators from the National Highway Traffic Safety Administration are closely monitoring the deployment, with Administrator Jennifer Park stating that comprehensive data collection will inform future autonomous vehicle policies nationwide. Early user reviews have been mixed, with commuters praising the smooth rides and competitive pricing at $0.85 per mile, while others report confusion about pickup locations and concerns about riding in vehicles without human operators during late-night hours.")
                .image("https://eu-images.contentstack.com/v3/assets/blt31d6b0704ba96e9d/blt771dc5c2e03d8c80/667df77fe96baf6a6960ff58/pic_5.jpg?width=1280&auto=webp&quality=80&format=jpg&disable=upscale")
                .reporter("Isabelle Robert")
                .dateTime(Instant.parse("2025-08-19T20:00:00Z"))
                .realVotes(2)
                .fakeVotes(19)
                .build();

        Comment c29_1 = Comment.builder()
                .username("Hubleaw no five")
                .text("I'm skeptical about this. Driverless taxis have been announced many times before and failed to take off. Let's see how long this lasts.")
                .image("https://i.pinimg.com/736x/68/00/48/68004889c41f09fa8fc06bdfc37b03ef.jpg")
                .time(Instant.parse("2025-08-20T00:00:00Z"))
                .vote(Vote.FAKE)
                .news(news29)
                .build();

        Comment c29_2 = Comment.builder()
                .username("Maria Bianchi")
                .text("I've been waiting for this for years! It's great to see driverless taxis finally becoming a reality in a major city.")
                .image(null)
                .time(Instant.parse("2025-08-19T22:00:00Z"))
                .vote(Vote.REAL)
                .news(news29)
                .build();

        Comment c29_3 = Comment.builder()
                .username("Marta Ruiz")
                .text("Driverless cars in a major city? Feels like they're jumping the gun. How can we trust this technology when it's still so new?")
                .image(null)
                .time(Instant.parse("2025-08-19T22:00:00Z"))
                .vote(Vote.FAKE)
                .news(news29)
                .build();

        news29.getComments().add(c29_1);
        news29.getComments().add(c29_2);
        news29.getComments().add(c29_3);
        newsDao.save(news29);

        // --- ข่าวที่ 30: Rare Animal ---
        News news30 = News.builder()
                .topic("Rare Animal Species Spotted After Decades")
                .shortDetail("The Javan elephant, believed extinct for 40 years, was photographed by camera traps in Indonesia's remote forests, offering new hope for conservation efforts.")
                .fullDetail("Wildlife researchers from the Indonesian Institute of Sciences have confirmed the existence of a small population of Javan elephants in the remote forests of West Java, marking the first verified sighting of the species in over four decades after it was presumed extinct due to deforestation and human encroachment. The breakthrough discovery came through motion-activated camera traps installed by Dr. Sari Indrawati's conservation team, which captured clear footage of at least six individuals including a mother with her calf foraging near a hidden water source in Gunung Halimun-Salak National Park. The Javan elephant, a subspecies distinct from Sumatran and Asian elephants, was last officially documented in 1982 before habitat destruction eliminated their known populations, making this rediscovery one of the most significant conservation stories of the decade. Indonesian Environment Minister Dr. Ahmad Sutrisno announced immediate emergency protection measures for the estimated 12-15 remaining individuals, including expanded patrol units and strict access restrictions to their habitat area. International elephant expert Dr. Lisa Thompson from the World Wildlife Fund called the discovery miraculous but emphasized the species remains critically endangered, requiring urgent genetic diversity studies and potential breeding programs to ensure long-term survival. Local communities in nearby villages reported occasional signs of large animals over the years but dismissed them as Sumatran elephants migrating from other regions, until DNA analysis of dung samples confirmed the unique genetic markers of the Javan subspecies. The Indonesian government has pledged $5 million in emergency conservation funding while collaborating with international organizations to develop a comprehensive species recovery plan, though experts warn that habitat protection and human-wildlife conflict mitigation will be crucial for preventing another near-extinction event.")
                .image("https://assets.globalwildlife.org/m/7550306bdae0c311/webimage-Rediscovered-Fernandina-Giant-Tortoise.jpg")
                .reporter("Michael Smith")
                .dateTime(Instant.parse("2025-08-20T20:00:00Z"))
                .realVotes(6)
                .fakeVotes(4)
                .build();

        Comment c30_1 = Comment.builder()
                .username("Peter Weber")
                .text("This sounds too good to be true. The Javan elephant has been gone for decades, and now it's suddenly 'discovered'? Doubtful.")
                .image("https://i.pinimg.com/736x/34/64/ad/3464ad1c33c983b87d66f14b092f11ee.jpg")
                .time(Instant.parse("2025-08-20T21:00:00Z"))
                .vote(Vote.FAKE)
                .news(news30)
                .build();

        Comment c30_2 = Comment.builder()
                .username("Sato Shota")
                .text("This sounds too convenient. How did they miss these elephants all this time? I doubt this will last.")
                .image(null)
                .time(Instant.parse("2025-08-21T00:00:00Z"))
                .vote(Vote.FAKE)
                .news(news30)
                .build();

        news30.getComments().add(c30_1);
        news30.getComments().add(c30_2);
        newsDao.save(news30);

        // --- ข่าวที่ 31: Moo Deng ---
        News news31 = News.builder()
                .topic("Here We Go! 'Moo Deng' Pygmy Hippo Meme Valued at Nearly 150 Million USD")
                .shortDetail("Moo Deng, a viral pygmy hippo from Khao Kiew Zoo, boosted zoo visits and appeared in ads. No royalties have been paid to the zoo. @hippo_cto recently donated 5 million THB, raising the coin's market cap to 143 million USD.")
                .fullDetail("Moo Deng, a 2-month-old pygmy hippo from Khao Kiew Zoo, became a viral sensation worldwide after its cute pictures and videos were shared on the Khamoo & The Gang Facebook page. The page originally posted adorable moments of animals at the zoo, such as capybaras and older hippos, which led to Moo Deng gaining widespread attention and helping the zoo attract over 100,000 visitors per month. This popularity turned Moo Deng into a social media superstar, beloved by fans of all ages. Although Moo Deng’s fame led to brands using its image in various ads and merchandise, such as t-shirts, bags, and billboards, there has been no news about any brand paying royalties to Khao Kiew Zoo. This raises questions about how the profits from this fame are shared. Moo Deng is not only a cute animal but has also become a valuable asset with significant economic impact. Recently, @hippo_cto, a cryptocurrency organization, donated 5 million THB to Khao Kiew Zoo, which boosted the market cap of the associated coin to 143 million USD. This highlights the financial worth of Moo Deng as not just a viral sensation but also a key player in the digital economy. However, questions about how copyrights and benefits from this fame are managed remain to be addressed, ensuring that Moo Deng's caregivers and Khao Kiew Zoo receive fair compensation for the viral success they’ve helped create.")
                .image("https://files-world.thaipbs.or.th/16_9_d662e87653.png")
                .reporter("Nina Wong")
                .dateTime(Instant.parse("2025-08-09T11:00:00Z"))
                .realVotes(46)
                .fakeVotes(46)
                .build();

        Comment c31_1 = Comment.builder()
                .username("Kaem W.")
                .text("It's so cute.")
                .image("https://s.isanook.com/jo/0/ud/494/2474469/m11.jpg?ip/resize/w728/q80/jpg")
                .time(Instant.parse("2025-08-09T13:00:00Z"))
                .vote(Vote.REAL)
                .news(news31)
                .build();

        Comment c31_2 = Comment.builder()
                .username("Mia Choi")
                .text("Seems like Moo Deng's fame is being used for profit without fair compensation to the zoo. Kinda sketchy.")
                .image(null)
                .time(Instant.parse("2025-08-09T14:00:00Z"))
                .vote(Vote.FAKE)
                .news(news31)
                .build();

        Comment c31_3 = Comment.builder()
                .username("Liam Phan")
                .text("Moo Deng is a star! But the zoo should definitely get paid for the brand deals.")
                .image("https://www.mp-uni.com/th/wp-content/uploads/sites/7/2024/09/hippo-moo-deng-01.jpg")
                .time(Instant.parse("2025-08-09T15:00:00Z"))
                .vote(Vote.REAL)
                .news(news31)
                .build();

        Comment c31_4 = Comment.builder()
                .username("Sophie Li")
                .text("It's just a hippo, though.")
                .image(null)
                .time(Instant.parse("2025-08-09T16:00:00Z"))
                .vote(Vote.FAKE)
                .news(news31)
                .build();

        news31.getComments().add(c31_1);
        news31.getComments().add(c31_2);
        news31.getComments().add(c31_3);
        news31.getComments().add(c31_4);
        newsDao.save(news31);
    }
}