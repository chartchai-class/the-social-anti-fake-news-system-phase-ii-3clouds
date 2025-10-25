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
        initDefaultUsers();

        if (newsDao.findAll().isEmpty()) {
            System.out.println("No news found. Initializing sample data...");
            initSampleData();
        } else {
            System.out.println("Database already populated.");
        }
    }

    private void initSampleData() {

        // --- ข่าวที่ 1: Ocean Cleanup Project ---
        News news1 = new News();
        news1.setTopic("Ocean Cleanup Project Removes 1,000 Tons of Plastic");
        news1.setShortDetail("The Ocean Cleanup Foundation reached a milestone by removing over 1,000 tons of plastic from the Great Pacific Garbage Patch...");
        news1.setFullDetail("In a groundbreaking achievement for marine environmental protection, The Ocean Cleanup Foundation reported that their latest System 03 vessel has successfully removed more than 1,000 tons of plastic debris from the Great Pacific Garbage Patch over the past six months, representing the most significant ocean plastic extraction operation to date. The innovative system, which uses advanced AI-guided collection nets and solar-powered propulsion, has demonstrated unprecedented efficiency in targeting microplastics and larger debris while minimizing impact on marine life through sophisticated bypass mechanisms. Founded by Dutch inventor Boyan Slat, the organization has been working since 2018 to develop scalable solutions for ocean plastic pollution, with this milestone representing a 300% improvement in collection rates compared to previous systems. Marine biologist Dr. Sarah Chen from the Pacific Marine Research Institute praised the achievement, stating that the removal of this volume of plastic could prevent the deaths of an estimated 50,000 marine animals annually, while oceanographer Professor James Mitchell noted that the project's data collection capabilities are providing valuable insights into ocean current patterns and plastic distribution. The collected plastic is being processed into consumer products through partnerships with major manufacturers, creating a circular economy model that the foundation plans to expand globally, with five additional System 03 vessels scheduled for deployment by 2026 across the world's most polluted ocean regions.");
        news1.setImage("https://assets.theoceancleanup.com/scaled/1200x675/app/uploads/2023/09/System03_Full_Span-scaled.jpg");
        news1.setReporter("Robert Clark");
        news1.setDateTime(Instant.parse("2025-08-09T11:00:00Z"));
        news1.setRealVotes(2);
        news1.setFakeVotes(14);

        Comment c1_1 = new Comment();
        c1_1.setUsername("Jack Phillips");
        c1_1.setText("Feels too good to be true, I'm skeptical.");
        c1_1.setImage(null);
        c1_1.setTime(Instant.parse("2025-08-09T15:00:00Z"));
        c1_1.setVote(Vote.FAKE);
        c1_1.setNews(news1);

        Comment c1_2 = new Comment();
        c1_2.setUsername("Zoey Adams");
        c1_2.setText("Love seeing technology used for environmental impact.");
        c1_2.setImage("https://i.pinimg.com/736x/24/f2/50/24f250c18fd7d70e15db4fb8aead4619.jpg");
        c1_2.setTime(Instant.parse("2025-08-09T13:00:00Z"));
        c1_2.setVote(Vote.REAL);
        c1_2.setNews(news1);

        Comment c1_3 = new Comment();
        c1_3.setUsername("Sirilak Srisuk");
        c1_3.setText("Until independent reports confirm, I won't fully trust this.");
        c1_3.setImage(null);
        c1_3.setTime(Instant.parse("2025-08-09T13:00:00Z"));
        c1_3.setVote(Vote.FAKE);
        c1_3.setNews(news1);

        Comment c1_4 = new Comment();
        c1_4.setUsername("Tanaka Ayaka");
        c1_4.setText("Officials provide updates on the situation.");
        c1_4.setImage("https://grist.org/wp-content/uploads/2019/10/theoceancleanup_october2nd_press_briefing_system001b-30-e1570213626974.jpg");
        c1_4.setTime(Instant.parse("2025-08-09T16:00:00Z"));
        c1_4.setVote(Vote.REAL);
        c1_4.setNews(news1);

        news1.getComments().add(c1_1);
        news1.getComments().add(c1_2);
        news1.getComments().add(c1_3);
        news1.getComments().add(c1_4);
        newsDao.save(news1);

        // --- ข่าวที่ 2: New Exoplanet ---
        News news2 = new News();
        news2.setTopic("Scientists Discover New Exoplanet Similar to Earth");
        news2.setShortDetail("International astronomers discover Kepler-452c, an Earth-like exoplanet 60% larger than Earth located in the habitable zone...");
        news2.setFullDetail("A team of international astronomers has made a groundbreaking discovery of Kepler-452c, a remarkable exoplanet located in the habitable zone of a star strikingly similar to our Sun, marking one of the most significant findings in the search for extraterrestrial life. The planet, approximately 60% larger than Earth with a rocky surface composition, orbits within the critical Goldilocks zone where temperatures allow liquid water to exist - a fundamental requirement for life as we know it. Advanced data analysis from the Kepler Space Telescope revealed that Kepler-452c is estimated to be 6 billion years old, making it significantly older than Earth and offering scientists a unique laboratory to study long-term planetary evolution and potential life development. Preliminary atmospheric observations suggest the planet may possess conditions capable of supporting life, though researchers emphasize that extensive follow-up studies are needed to confirm these promising indicators. Dr. Maria Rodriguez, lead astronomer on the discovery team, stated that this finding represents a major leap forward in our understanding of planetary habitability and brings us closer than ever to answering whether we are alone in the universe. The discovery not only advances our knowledge of planetary formation but also establishes crucial groundwork for future space missions designed to detect biosignatures and explore potentially habitable worlds beyond our solar system.");
        news2.setImage("https://science.nasa.gov/wp-content/uploads/2024/01/toi715b1280-illo.jpg?resize=900,506");
        news2.setReporter("Emily Garcia");
        news2.setDateTime(Instant.parse("2025-08-14T16:27:45.406644Z"));
        news2.setRealVotes(210);
        news2.setFakeVotes(25);

        Comment c2_1 = new Comment();
        c2_1.setUsername("Guillaume Leclerc");
        c2_1.setText("Wow! This could change how we think about habitable planets. 🚀");
        c2_1.setImage("https://www.universetoday.com/article_images/helix-nebula.jpeg");
        c2_1.setTime(Instant.parse("2025-08-14T13:15:00Z"));
        c2_1.setVote(Vote.REAL);
        c2_1.setNews(news2);

        Comment c2_2 = new Comment();
        c2_2.setUsername("Su young");
        c2_2.setText("Another Earth-like planet? We've seen this before, nothing new.");
        c2_2.setImage(null);
        c2_2.setTime(Instant.parse("2025-08-14T13:50:00Z"));
        c2_2.setVote(Vote.FAKE);
        c2_2.setNews(news2);

        news2.getComments().add(c2_1);
        news2.getComments().add(c2_2);
        newsDao.save(news2);

        // --- ข่าวที่ 3: Cancer Treatment ---
        News news3 = new News();
        news3.setTopic("Major Breakthrough in Cancer Treatment Announced");
        news3.setShortDetail("Scientists at Global Health Institute develop revolutionary cancer therapy combining CRISPR gene editing and personalized immunotherapy, achieving 85% response rates in early trials and offering new hope for advanced-stage cancer patients.");
        news3.setFullDetail("Researchers at the Global Health Institute have announced a revolutionary cancer treatment breakthrough that combines cutting-edge CRISPR gene editing technology with personalized immunotherapy, demonstrating unprecedented success in early clinical trials with an 85% response rate among advanced-stage cancer patients. The innovative therapy works by precisely editing a patient's immune cells to enhance their ability to recognize and destroy cancer cells while leaving healthy tissue completely unharmed, addressing one of the most challenging aspects of traditional cancer treatment. Unlike conventional chemotherapy and radiation that often cause severe side effects, this personalized approach trains each patient's own immune system to target malignant cells with surgical precision, dramatically reducing adverse reactions and improving quality of life during treatment. Dr. Sarah Kim, lead researcher and oncologist, explained that this represents a paradigm shift from attacking cancer with external toxins to empowering the body's natural defense mechanisms with molecular-level precision. The treatment protocol involves extracting immune cells from patients, genetically modifying them in specialized laboratories, and reintroducing them to fight cancer more effectively than ever before. Phase II trials involving 300 patients across multiple cancer types are scheduled to begin next year, with researchers optimistic that the therapy could receive regulatory approval within five years and become a standard treatment option, potentially transforming cancer from a terminal diagnosis into a manageable condition for millions of patients worldwide.");
        news3.setImage("https://assets.weforum.org/article/image/23tEFwxzFGXY0PkZPE_0zVBrK-86zupQa99xMOKhxqY.jpg");
        news3.setReporter("Michael Lee");
        news3.setDateTime(Instant.parse("2025-08-14T16:27:45.406652Z"));
        news3.setRealVotes(300);
        news3.setFakeVotes(12);

        Comment c3_1 = new Comment();
        c3_1.setUsername("David Kim");
        c3_1.setText("This gives hope to millions of patients worldwide. ❤️");
        c3_1.setImage(null);
        c3_1.setTime(Instant.parse("2025-08-14T15:00:00Z"));
        c3_1.setVote(Vote.REAL);
        c3_1.setNews(news3);

        Comment c3_2 = new Comment();
        c3_2.setUsername("Margaux Fontaine");
        c3_2.setText("Can't wait for this to be available to everyone.");
        c3_2.setImage("https://i.pinimg.com/736x/ba/92/7f/ba927ff34cd961ce2c184d47e8ead9f6.jpg");
        c3_2.setTime(Instant.parse("2025-08-14T15:40:00Z"));
        c3_2.setVote(Vote.REAL);
        c3_2.setNews(news3);

        news3.getComments().add(c3_1);
        news3.getComments().add(c3_2);
        newsDao.save(news3);

        // --- ข่าวที่ 4: SpaceX Launch ---
        News news4 = new News();
        news4.setTopic("SpaceX Successfully Launches New Rocket");
        news4.setShortDetail("SpaceX achieves its 20th successful launch of 2025 with the debut flight of its enhanced Falcon Heavy Block 6, deploying three commercial satellites and testing revolutionary methane-based propulsion technology.");
        news4.setFullDetail("SpaceX marked another historic milestone early this morning with the flawless launch of its enhanced Falcon Heavy Block 6 rocket from Kennedy Space Center, achieving the company's 20th successful mission of 2025 and demonstrating breakthrough advancements in space transportation technology. The dawn launch, witnessed by over 15,000 spectators gathered along Florida's Space Coast, successfully deployed three commercial satellites into precise orbits while testing revolutionary methane-based Raptor engines designed to significantly reduce launch costs and increase payload capacity by 40%. The mission showcased SpaceX's new autonomous flight termination system and advanced grid fins made from titanium, technologies that will be crucial for the company's ambitious Artemis lunar missions and Mars colonization program. CEO Elon Musk celebrated the achievement on social media, stating that today's success brings us one step closer to making life multi-planetary, while SpaceX engineers confirmed that all three booster cores successfully returned to Earth and landed simultaneously at Cape Canaveral, setting a new record for precision recovery operations. Industry analysts note that this technological leap strengthens SpaceX's dominant position in the $400 billion global space economy, with the company now holding contracts worth over $10 billion for upcoming NASA missions, commercial satellite deployments, and space tourism ventures. The successful integration of these next-generation systems positions SpaceX to begin crewed lunar missions by 2027 and establishes the foundation for the planned Mars missions scheduled for the 2030s, marking a pivotal moment in humanity's expansion beyond Earth.");
        news4.setImage("https://spaceflightnow.com/wp-content/uploads/2023/05/20230510star2-9.jpeg");
        news4.setReporter("John Lee");
        news4.setDateTime(Instant.parse("2025-08-14T16:27:45.406667Z"));
        news4.setRealVotes(250);
        news4.setFakeVotes(8);

        Comment c4_1 = new Comment();
        c4_1.setUsername("SpaceFan99");
        c4_1.setText("Amazing! Space travel is really becoming normal.");
        c4_1.setImage(null);
        c4_1.setTime(Instant.parse("2025-08-14T16:30:00Z"));
        c4_1.setVote(Vote.REAL);
        c4_1.setNews(news4);

        Comment c4_2 = new Comment();
        c4_2.setUsername("Skeptic101");
        c4_2.setText("Photos might be edited, verify the source.");
        c4_2.setImage(null);
        c4_2.setTime(Instant.parse("2025-08-14T16:50:00Z"));
        c4_2.setVote(Vote.FAKE);
        c4_2.setNews(news4);

        news4.getComments().add(c4_1);
        news4.getComments().add(c4_2);
        newsDao.save(news4);

        // --- ข่าวที่ 5: Major Flood ---
        News news5 = new News();
        news5.setTopic("Major Flood Hits Downtown City");
        news5.setShortDetail("Heavy rainfall causes severe flooding in downtown city, prompting evacuations and widespread damage to infrastructure.");
        news5.setFullDetail("Record-breaking rainfall has caused severe flooding in downtown areas, submerging roads, inundating buildings, and causing significant damage to local infrastructure. The floodwaters have disrupted transportation, leaving many roads impassable and stranding residents. Emergency services have evacuated over 5,000 people from affected areas, while local shelters have been set up to provide temporary housing, food, and medical aid. Authorities have warned of the heightened risk of waterborne diseases, urging citizens to stay away from floodwaters and remain in safe locations. In addition to immediate relief efforts, teams are distributing clean water, food, and medical supplies to those in need. Experts are raising concerns that climate change has led to more frequent and intense storms, and they are urging governments to prioritize urban planning reforms to reduce the risk of future disasters. The devastation has left many people without power and basic necessities, and recovery efforts are expected to continue for weeks.");
        news5.setImage("https://th-i.thgim.com/public/incoming/arg1a9/article68392085.ece/alternates/FREE_1200/PTI07_11_2024_000035A.jpg");
        news5.setReporter("Emily Garcia");
        news5.setDateTime(Instant.parse("2025-08-14T16:27:45.406671Z"));
        news5.setRealVotes(180);
        news5.setFakeVotes(20);

        Comment c5_1 = new Comment();
        c5_1.setUsername("Alex Rivers");
        c5_1.setText("Hope everyone affected stays safe. Sending thoughts!");
        c5_1.setImage(null);
        c5_1.setTime(Instant.parse("2025-08-14T18:00:00Z"));
        c5_1.setVote(Vote.REAL);
        c5_1.setNews(news5);

        Comment c5_2 = new Comment();
        c5_2.setUsername("DoubtfulReader");
        c5_2.setText("I think some reports are exaggerated, check local sources.");
        c5_2.setImage(null);
        c5_2.setTime(Instant.parse("2025-08-14T18:30:00Z"));
        c5_2.setVote(Vote.FAKE);
        c5_2.setNews(news5);

        news5.getComments().add(c5_1);
        news5.getComments().add(c5_2);
        newsDao.save(news5);

        // --- ข่าวที่ 6: Stock Market ---
        News news6 = new News();
        news6.setTopic("Stock Market Hits Record High Amid Tech Surge");
        news6.setShortDetail("Global markets hit record highs as tech giants exceed Q3 earnings expectations, with Nasdaq surging 4.2% and AI stocks gaining $800 billion in value.");
        news6.setFullDetail("Global financial markets celebrated a historic milestone today as major stock indices soared to unprecedented levels, led by an explosive rally in technology stocks that saw the Nasdaq Composite Index surge 4.2% to close at an all-time high of 18,750 points. The remarkable rally was fueled by blockbuster third-quarter earnings reports from tech giants including Apple, Microsoft, Google, and NVIDIA, which collectively exceeded analyst expectations by an average of 22%, generating over $800 billion in additional market capitalization for the artificial intelligence and renewable technology sectors. Investor enthusiasm reached fever pitch as AI-focused companies reported revenue growth exceeding 150% year-over-year, while clean energy stocks benefited from massive government infrastructure investments and corporate sustainability commitments. Market analyst Sarah Williams from Goldman Sachs commented that we're witnessing a fundamental shift in how investors value technology innovation, with AI and green tech now representing the primary drivers of economic growth for the next decade. However, veteran financial advisors are urging caution amid the exuberance, noting that rapid market gains often precede periods of increased volatility, and recommending that investors maintain diversified portfolios while avoiding overexposure to high-growth tech stocks. Trading volumes hit record levels with over $2.5 trillion in daily transactions, while institutional investors poured $45 billion into technology-focused ETFs this week alone, signaling sustained confidence in the sector's long-term prospects despite warnings about potential market corrections in the coming quarters.");
        news6.setImage("https://www.investopedia.com/thmb/L6KR0VU3zbZf8_ZBBuazL_UsRn8=/750x0/filters:no_upscale():max_bytes(150000):strip_icc():format(webp)/GettyImages-2229156936-61795d1806f6492ab9de6ba6212917fc.jpg");
        news6.setReporter("Michael Brown");
        news6.setDateTime(Instant.parse("2025-08-14T16:27:45.406674Z"));
        news6.setRealVotes(220);
        news6.setFakeVotes(15);

        Comment c6_1 = new Comment();
        c6_1.setUsername("InvestorMike");
        c6_1.setText("Great news for the economy, looking to invest more!");
        c6_1.setImage(null);
        c6_1.setTime(Instant.parse("2025-08-14T19:00:00Z"));
        c6_1.setVote(Vote.REAL);
        c6_1.setNews(news6);

        Comment c6_2 = new Comment();
        c6_2.setUsername("CautiousAnalyst");
        c6_2.setText("Be careful, markets might correct soon.");
        c6_2.setImage(null);
        c6_2.setTime(Instant.parse("2025-08-14T19:20:00Z"));
        c6_2.setVote(Vote.FAKE);
        c6_2.setNews(news6);

        news6.getComments().add(c6_1);
        news6.getComments().add(c6_2);
        newsDao.save(news6);

        // --- ข่าวที่ 7: Football Championship ---
        News news7 = new News();
        news7.setTopic("Local Football Team Wins National Championship");
        news7.setShortDetail("City Eagles defeat defending champions 28-21 in overtime thriller to claim their first national championship in 15 years, sparking massive celebrations across the city.");
        news7.setFullDetail("The City Eagles captured their first national championship in 15 years with a dramatic 28-21 overtime victory over the defending champion Metro Lions at National Stadium, capping off a perfect 14-0 season that has electrified the entire community and established the team as one of the greatest in franchise history. The nail-biting final saw quarterback Jake Morrison throw the game-winning touchdown pass with just 2:47 remaining in overtime, while the Eagles' defense intercepted three crucial passes to seal the victory before a sellout crowd of 75,000 fans and millions watching nationwide. Within minutes of the final whistle, over 100,000 jubilant fans flooded downtown streets in spontaneous celebrations that continued past midnight, prompting Mayor Patricia Johnson to declare Monday a city holiday and announce plans for a championship parade expected to draw 500,000 people. Head coach Michael Rodriguez, who led the team's remarkable turnaround from last place just three years ago, praised his players unbreakable spirit and dedication, while star running back Tommy Chen was named championship MVP after rushing for 185 yards and two touchdowns. The victory has generated an estimated $50 million economic boost for the city through increased tourism, merchandise sales, and national media attention, while inspiring thousands of young athletes in local youth programs who see the Eagles as proof that dreams can become reality through hard work and determination.");
        news7.setImage("https://vtdigger.org/wp-content/uploads/2025/08/vermont-green-2-20250802-2000x1330.jpg");
        news7.setReporter("Emily Miller");
        news7.setDateTime(Instant.parse("2025-08-14T16:27:45.406677Z"));
        news7.setRealVotes(300);
        news7.setFakeVotes(5);

        Comment c7_1 = new Comment();
        c7_1.setUsername("SoccerFan88");
        c7_1.setText("Incredible match! Fans are ecstatic!");
        c7_1.setImage(null);
        c7_1.setTime(Instant.parse("2025-08-14T20:00:00Z"));
        c7_1.setVote(Vote.REAL);
        c7_1.setNews(news7);

        Comment c7_2 = new Comment();
        c7_2.setUsername("DoubterGuy");
        c7_2.setText("Maybe highlights are exaggerated.");
        c7_2.setImage(null);
        c7_2.setTime(Instant.parse("2025-08-14T20:30:00Z"));
        c7_2.setVote(Vote.FAKE);
        c7_2.setNews(news7);

        news7.getComments().add(c7_1);
        news7.getComments().add(c7_2);
        newsDao.save(news7);

        // --- ข่าวที่ 8: Art Exhibition ---
        News news8 = new News();
        news8.setTopic("New Art Exhibition Opens Downtown");
        news8.setShortDetail("Voices of Tomorrow art exhibition opens at Metropolitan Gallery, featuring 150 works from 45 local and international artists exploring themes of urban transformation and cultural identity.");
        news8.setFullDetail("The highly anticipated Voices of Tomorrow art exhibition opened its doors at the prestigious Metropolitan Gallery downtown, drawing over 2,000 visitors on opening night to experience an extraordinary collection of 150 contemporary artworks from 45 emerging local artists and internationally acclaimed figures including renowned sculptor Maria Santos and digital artist Chen Wei-Ming. The groundbreaking exhibition, curated by Dr. Amanda Foster, explores powerful themes of urban transformation, climate resilience, and cultural diversity through a stunning array of interactive installations, immersive digital displays, and thought-provoking mixed-media pieces that invite visitors to engage directly with pressing social and environmental issues. Highlights include a 20-foot kinetic sculpture representing rising sea levels, a virtual reality experience showcasing the city's evolution over the past century, and a collaborative mural created by 12 local high school students that celebrates the community's multicultural heritage. Gallery director Robert Kim announced that the two-month exhibition will feature an extensive program of weekly artist talks, hands-on workshops for all ages, and specialized guided tours led by the artists themselves, with proceeds from artwork sales supporting the city's emerging artist grant program. The opening weekend alone generated $75,000 in art sales and attracted cultural tourists from neighboring states, positioning the exhibition as a significant cultural and economic catalyst for the downtown arts district while providing a vital platform for local creative talent to gain national recognition.");
        news8.setImage("https://culture360.asef.org/media/2017/11/295chD8_germanart_loans05.jpg");
        news8.setReporter("Michael Davis");
        news8.setDateTime(Instant.parse("2025-08-14T16:27:45.406683Z"));
        news8.setRealVotes(110);
        news8.setFakeVotes(8);

        Comment c8_1 = new Comment();
        c8_1.setUsername("Ratti P.");
        c8_1.setText("Beautiful curation! Definitely worth visiting.");
        c8_1.setImage(null);
        c8_1.setTime(Instant.parse("2025-08-14T10:00:00Z"));
        c8_1.setVote(Vote.REAL);
        c8_1.setNews(news8);

        Comment c8_2 = new Comment();
        c8_2.setUsername("SkepticalViewer");
        c8_2.setText("Some pieces look exaggerated, not sure if authentic.");
        c8_2.setImage(null);
        c8_2.setTime(Instant.parse("2025-08-14T10:20:00Z"));
        c8_2.setVote(Vote.FAKE);
        c8_2.setNews(news8);

        news8.getComments().add(c8_1);
        news8.getComments().add(c8_2);
        newsDao.save(news8);

        // --- ข่าวที่ 9: Tech Conference ---
        News news9 = new News();
        news9.setTopic("Tech Conference Announced with Key Industry Leaders");
        news9.setShortDetail("TechGlobal Summit 2025 announces star-studded lineup featuring Elon Musk, Satya Nadella, and 200+ industry leaders for three-day innovation showcase expected to draw 25,000 attendees.");
        news9.setFullDetail("The prestigious TechGlobal Summit 2025 has officially announced its impressive lineup of keynote speakers and industry leaders, scheduled for September 15-17 at the Los Angeles Convention Center, with confirmed appearances by Tesla CEO Elon Musk, Microsoft CEO Satya Nadella, OpenAI's Sam Altman, and over 200 technology executives representing companies valued at more than $2 trillion collectively. The three-day innovation showcase is expected to attract 25,000 participants from 80 countries, including venture capitalists managing $500 billion in tech investments, startup founders, and government officials focused on digital transformation and technology policy. Conference highlights include Musk's highly anticipated keynote on The Future of Autonomous Systems, panel discussions on AI regulation featuring top government officials, cybersecurity workshops led by former NSA experts, and a sustainability tech pavilion showcasing solutions for climate change mitigation. Event organizer Jennifer Park, CEO of Global Tech Events, emphasized that this year's summit represents the largest gathering of technology leaders since the pandemic, creating unprecedented opportunities for collaboration, investment, and breakthrough innovations that will shape the next decade. The conference will feature a startup pitch competition with $10 million in total prize money, exclusive investor networking sessions, and live demonstrations of cutting-edge technologies including quantum computing, brain-computer interfaces, and next-generation renewable energy systems, establishing the summit as the premier destination for anyone seeking to understand and influence the future of technology.");
        news9.setImage("https://www.traveldailymedia.com/assets/2020/01/techsauce.jpg");
        news9.setReporter("Sarah Williams");
        news9.setDateTime(Instant.parse("2025-08-14T16:27:45.406687Z"));
        news9.setRealVotes(180);
        news9.setFakeVotes(12);

        Comment c9_1 = new Comment();
        c9_1.setUsername("TechFan99");
        c9_1.setText("Can't wait! Looks like a great lineup of speakers.");
        c9_1.setImage(null);
        c9_1.setTime(Instant.parse("2025-08-14T11:30:00Z"));
        c9_1.setVote(Vote.REAL);
        c9_1.setNews(news9);

        Comment c9_2 = new Comment();
        c9_2.setUsername("CautiousReporter");
        c9_2.setText("Check official site before registering, sometimes announcements are exaggerated.");
        c9_2.setImage(null);
        c9_2.setTime(Instant.parse("2025-08-14T11:50:00Z"));
        c9_2.setVote(Vote.FAKE);
        c9_2.setNews(news9);

        news9.getComments().add(c9_1);
        news9.getComments().add(c9_2);
        newsDao.save(news9);

        // --- ข่าวที่ 10: Café Chain Expansion ---
        News news10 = new News();
        news10.setTopic("New Café Chain Expands Internationally");
        news10.setShortDetail("Popular local café chain announces $250 million international expansion, opening 150 stores across 12 countries with plans to compete directly with Starbucks in the global market.");
        news10.setFullDetail("The rapidly growing artisanal café chain that started as a single coffee shop just eight years ago announced its ambitious $250 million international expansion plan that will see 150 flagship stores opening across 12 countries in Europe and Asia over the next 18 months, positioning the brand as a serious challenger to industry giants like Starbucks and Costa Coffee. The expansion strategy begins with premium locations in London, Paris, Tokyo, Seoul, and Singapore, featuring locally-sourced ingredients and regionally-adapted menu items such as matcha-infused pastries in Japan and Nordic-inspired breakfast bowls in Scandinavian markets, while maintaining the brand's signature commitment to fair-trade coffee and zero-waste operations. CEO Maria Rodriguez revealed that the company has secured strategic partnerships with local suppliers in each target market and invested heavily in sustainable packaging technology, including compostable cups and plant-based food containers that align with European environmental regulations. The international rollout will introduce the company's innovative Coffee Passport loyalty program, allowing customers to earn rewards across all global locations, alongside a cutting-edge mobile ordering system that uses AI to predict customer preferences and reduce wait times. Industry analysts project that this bold expansion could capture 3% of the global specialty coffee market within five years, potentially generating over $1 billion in annual revenue and establishing the café chain as the first American coffee brand to successfully challenge established European coffee culture while maintaining its commitment to environmental sustainability and community engagement.");
        news10.setImage("https://perfectdailygrind.com/wp-content/uploads/2022/10/airport-coffee-1024x640.jpg");
        news10.setReporter("Robert Garcia");
        news10.setDateTime(Instant.parse("2025-08-14T16:27:45.406690Z"));
        news10.setRealVotes(140);
        news10.setFakeVotes(10);

        Comment c10_1 = new Comment();
        c10_1.setUsername("CoffeeLover");
        c10_1.setText("Exciting! I hope they come to my city soon.");
        c10_1.setImage(null);
        c10_1.setTime(Instant.parse("2025-08-14T12:10:00Z"));
        c10_1.setVote(Vote.REAL);
        c10_1.setNews(news10);

        Comment c10_2 = new Comment();
        c10_2.setUsername("Skeptic123");
        c10_2.setText("International expansion often faces challenges, hope they succeed.");
        c10_2.setImage(null);
        c10_2.setTime(Instant.parse("2025-08-14T12:40:00Z"));
        c10_2.setVote(Vote.FAKE);
        c10_2.setNews(news10);

        news10.getComments().add(c10_1);
        news10.getComments().add(c10_2);
        newsDao.save(news10);

        // --- ข่าวที่ 11: AI Novel Writing ---
        News news11 = new News();
        news11.setTopic("New AI Model Can Write Novels in Seconds");
        news11.setShortDetail("Breakthrough AI model NovelGPT generates complete 300-page novels in under 30 seconds, sparking debate among publishers and authors about the future of creative writing.");
        news11.setFullDetail("Researchers at Stanford University's AI Laboratory have unveiled NovelGPT, a revolutionary artificial intelligence model capable of generating complete, coherent novels of up to 300 pages in less than 30 seconds, representing a quantum leap in AI creative capabilities that has sent shockwaves through the publishing industry and literary community worldwide. The advanced neural network, trained on over 100,000 published novels across multiple genres, can produce original storylines, develop complex characters, and maintain narrative consistency throughout entire books, with early demonstrations showing the AI creating detective mysteries, romance novels, and science fiction epics that are virtually indistinguishable from human-written works. Publishing executives report receiving dozens of AI-generated manuscripts weekly, while major publishers like Penguin Random House have established new review protocols specifically for AI-authored content, raising unprecedented questions about authorship, creativity, and intellectual property rights in the digital age. Literary critic Dr. James Patterson expressed concerns that we're witnessing the potential democratization of novel writing, but also the possible devaluation of human creativity, while bestselling author Margaret Chen argued that AI can replicate patterns and structures, but it cannot capture the authentic human experience that makes great literature truly resonate with readers. The technology's creators estimate that NovelGPT could enable aspiring writers to produce first drafts instantly, revolutionize content creation for streaming platforms seeking original stories, and potentially flood the market with AI-generated literature, fundamentally altering how society defines and values human artistic expression in an increasingly automated world.");
        news11.setImage("https://miro.medium.com/v2/resize:fit:1200/1*4ETPeLePkGr0qjZrdsOB4Q.jpeg");
        news11.setReporter("Emily Davis");
        news11.setDateTime(Instant.parse("2025-08-01T10:00:00Z"));
        news11.setRealVotes(29);
        news11.setFakeVotes(14);

        Comment c11_1 = new Comment();
        c11_1.setUsername("Ping");
        c11_1.setText("AI can't replicate true human creativity.");
        c11_1.setImage("https://i.pinimg.com/736x/bf/6e/29/bf6e296386c67b027cd3d234e3c6efa4.jpg");
        c11_1.setTime(Instant.parse("2025-08-01T12:00:00Z"));
        c11_1.setVote(Vote.FAKE);
        c11_1.setNews(news11);

        Comment c11_2 = new Comment();
        c11_2.setUsername("Jinhee Kwak");
        c11_2.setText("Incredible technology! It could change the future of publishing, but I wonder how it will impact writers.");
        c11_2.setImage(null);
        c11_2.setTime(Instant.parse("2025-08-01T15:00:00Z"));
        c11_2.setVote(Vote.REAL);
        c11_2.setNews(news11);

        Comment c11_3 = new Comment();
        c11_3.setUsername("Harper Harris");
        c11_3.setText("This feels like a real turning point. The evidence makes it hard to doubt—AI really is moving faster than we ever imagined.");
        c11_3.setImage("https://i.pinimg.com/736x/aa/91/1a/aa911af80d78efb920dbbb37b8c94090.jpg");
        c11_3.setTime(Instant.parse("2025-08-01T15:00:00Z"));
        c11_3.setVote(Vote.REAL);
        c11_3.setNews(news11);

        news11.getComments().add(c11_1);
        news11.getComments().add(c11_2);
        news11.getComments().add(c11_3);
        newsDao.save(news11);

        // --- ข่าวที่ 12: Electric Bus Service (FAKE) ---
        News news12 = new News();
        news12.setTopic("City Introduces Free Electric Bus Service");
        news12.setShortDetail("City launches revolutionary free electric bus network with 25 routes and 80 vehicles powered by experimental fusion technology, promising to eliminate all traffic pollution overnight.");
        news12.setFullDetail("In a stunning announcement that has left transportation experts baffled, the city has launched what officials claim is the world's first fusion-powered electric bus fleet, featuring 80 vehicles equipped with miniaturized nuclear fusion reactors that allegedly produce zero emissions while generating unlimited clean energy. Mayor Sarah Thompson held a press conference surrounded by the gleaming white buses, declaring that this breakthrough technology, developed in secret partnership with undisclosed international scientists, will revolutionize urban transportation forever and make our city completely carbon-neutral within 30 days. The buses, which city officials insist can operate continuously for months without recharging, feature mysterious glowing panels and emit a faint humming sound that transportation engineers admit they cannot explain. Dr. Michael Rodriguez, a so-called quantum transportation specialist who appeared via video link from an undisclosed location, claimed that the fusion reactors are smaller than a briefcase yet more powerful than traditional power plants, while refusing to provide technical specifications citing national security concerns. Strangely, no major news outlets have been allowed to inspect the buses' interiors, and several independent scientists who requested access to examine the technology have been turned away by city security. Despite these unusual circumstances, ridership has reportedly reached an impossible 50,000 passengers daily on buses that appear to hold only 40 people each, leading some residents to question whether the entire program might be an elaborate publicity stunt designed to attract federal green energy funding.");
        news12.setImage("https://i0.wp.com/southeastasiainfra.com/wp-content/uploads/2023/08/ThaiSmile-buses.jpeg?resize=1200%2C640&ssl=1");
        news12.setReporter("Emily Davis");
        news12.setDateTime(Instant.parse("2025-08-02T18:00:00Z"));
        news12.setRealVotes(9);
        news12.setFakeVotes(23);

        Comment c12_1 = new Comment();
        c12_1.setUsername("Lucie Bernard");
        c12_1.setText("Experts weigh in on controversial topic.");
        c12_1.setImage(null);
        c12_1.setTime(Instant.parse("2025-08-02T19:00:00Z"));
        c12_1.setVote(Vote.REAL);
        c12_1.setNews(news12);

        Comment c12_2 = new Comment();
        c12_2.setUsername("Elizabeth Garcia");
        c12_2.setText("Fusion-powered buses? That sounds way too good to be true. The lack of verifiable details and scientific skepticism makes me think this is just an elaborate publicity stunt.");
        c12_2.setImage("https://i.pinimg.com/564x/d2/6f/ae/d26fae915e8781b2a16dc5a48673efac.jpg");
        c12_2.setTime(Instant.parse("2025-08-02T21:00:00Z"));
        c12_2.setVote(Vote.FAKE);
        c12_2.setNews(news12);

        Comment c12_3 = new Comment();
        c12_3.setUsername("Thanayapat Ch.");
        c12_3.setText("Fusion-powered buses? I really doubt this. If it were real, there would be far more scientific reports and technical details available.");
        c12_3.setImage("https://encrypted-tbn2.gstatic.com/images?q=tbn:ANd9GcQA9ykkfWX7n5Hrn0RYeoBa0mf7eErQvuTXM69inQaf_fwEYaei1WMsZ7pQg3sg");
        c12_3.setTime(Instant.parse("2025-08-02T22:00:00Z"));
        c12_3.setVote(Vote.FAKE);
        c12_3.setNews(news12);

        Comment c12_4 = new Comment();
        c12_4.setUsername("Guo Shan");
        c12_4.setText("Healing cancer with headphones in three days? Sorry, but that sounds more like science fiction than actual medicine.");
        c12_4.setImage("https://f.ptcdn.info/230/088/000/mbj2qtr0iMqzk96q3TF-o.jpg");
        c12_4.setTime(Instant.parse("2025-08-02T20:00:00Z"));
        c12_4.setVote(Vote.FAKE);
        c12_4.setNews(news12);

        news12.getComments().add(c12_1);
        news12.getComments().add(c12_2);
        news12.getComments().add(c12_3);
        news12.getComments().add(c12_4);
        newsDao.save(news12);

        // --- ข่าวที่ 13: TOI-715b Discovery ---
        News news13 = new News();
        news13.setTopic("Scientists Discover New Planet in Nearby Solar System");
        news13.setShortDetail("Astronomers discover TOI-715b, a super-Earth exoplanet 137 light-years away in the habitable zone of its star, potentially capable of supporting liquid water and life.");
        news13.setFullDetail("An international team of astronomers has announced the discovery of TOI-715b, a fascinating super-Earth exoplanet located 137 light-years away in the constellation Pictor, marking one of the most promising potentially habitable worlds found in recent years through data collected by NASA's Transiting Exoplanet Survey Satellite (TESS). The newly discovered planet, approximately 1.55 times the size of Earth, orbits within the habitable zone of its red dwarf star TOI-715, completing one orbit every 19.3 Earth days in conditions that could theoretically allow liquid water to exist on its surface. Lead researcher Dr. Georgina Dransfield from the University of Birmingham explained that TOI-715b represents an exciting target for follow-up observations with the James Webb Space Telescope, which could potentially detect atmospheric signatures that might indicate the presence of water vapor or even biosignatures. The discovery is particularly significant because red dwarf stars, which comprise about 75% of all stars in our galaxy, have extremely long lifespans that could provide stable conditions for billions of years, making them prime candidates for hosting life-sustaining planets. Additional observations have also revealed evidence of a second planet candidate in the same system, designated TOI-715c, which would be the smallest habitable-zone planet discovered by TESS if confirmed. Astrophysicist Dr. Emily Gilbert from the Jet Propulsion Laboratory noted that this system offers us a unique laboratory to study planetary formation and evolution around smaller stars, while emphasizing that future atmospheric studies will be crucial in determining whether these worlds could actually support life as we know it.");
        news13.setImage("https://assets.science.nasa.gov/dynamicimage/assets/science/astro/exo-explore/2023/09/k/kepler_all-planets_may20161280.jpg");
        news13.setReporter("John Miller");
        news13.setDateTime(Instant.parse("2025-08-03T20:00:00Z"));
        news13.setRealVotes(18);
        news13.setFakeVotes(6);

        Comment c13_1 = new Comment();
        c13_1.setUsername("Abigail Thompson");
        c13_1.setText("Feels too optimistic about habitability.");
        c13_1.setImage("https://i0.wp.com/www.gktoday.in/wp-content/uploads/2024/02/202402054101.png?w=328&ssl=1");
        c13_1.setTime(Instant.parse("2025-08-04T01:00:00Z"));
        c13_1.setVote(Vote.FAKE);
        c13_1.setNews(news13);

        Comment c13_2 = new Comment();
        c13_2.setUsername("Pratchaya Srichok");
        c13_2.setText("Until follow-up observations confirm, I won't trust it.");
        c13_2.setImage(null);
        c13_2.setTime(Instant.parse("2025-08-03T21:00:00Z"));
        c13_2.setVote(Vote.FAKE);
        c13_2.setNews(news13);

        news13.getComments().add(c13_1);
        news13.getComments().add(c13_2);
        newsDao.save(news13);

        // --- ข่าวที่ 14: Wearable Translator (FAKE) ---
        News news14 = new News();
        news14.setTopic("Startup Launches Wearable Translator Earbuds");
        news14.setShortDetail("Tech startup claims revolutionary MindLink earbuds can translate any language instantly through direct brain wave interpretation, raising concerns among linguists and neuroscientists.");
        news14.setFullDetail("Silicon Valley startup NeuroLingo has unveiled what it claims are the world's first telepathic translation earbuds called MindLink, which allegedly bypass traditional audio processing by directly reading users' brain waves to provide instant translation of any spoken language, including ancient dialects and even animal communication patterns. Company CEO Dr. Alexandra Chen, who holds what she describes as classified degrees in quantum linguistics, demonstrated the device at a secretive press event where reporters were not allowed to bring recording equipment, claiming the earbuds successfully translated conversations with dolphins, decoded 3,000-year-old Sanskrit texts, and even interpreted the emotional frequencies of houseplants. The earbuds, priced at an astounding $50,000 per pair, supposedly contain microscopic quantum processors that can synchronize with the universal language matrix, according to company materials that cite research from universities that fact-checkers have been unable to verify. Linguistics professor Dr. Maria Rodriguez from Stanford expressed skepticism, stating that the fundamental claims about brain-wave translation contradict everything we know about how language processing works, while neuroscientist Dr. James Park noted that no current technology can decode complex thoughts directly from brain activity with this level of precision. Despite these concerns, NeuroLingo reports receiving over 10,000 pre-orders from customers including unnamed government agencies and intergalactic communication researchers, though the company has refused to provide any independent testing data or allow scientific examination of their prototype devices, fueling speculation that the entire venture may be an elaborate cryptocurrency investment scheme.");
        news14.setImage("https://image.bastillepost.com/1200x/wp-content/uploads/global/2025/03/8419803_1741919148002_a_FB.jpg.webp");
        news14.setReporter("Alice Johnson");
        news14.setDateTime(Instant.parse("2025-08-04T10:00:00Z"));
        news14.setRealVotes(13);
        news14.setFakeVotes(25);

        Comment c14_1 = new Comment();
        c14_1.setUsername("Wipada Boonyasang");
        c14_1.setText("Could actually work with quantum tech.");
        c14_1.setImage("https://i.pinimg.com/736x/5a/ec/22/5aec2220c6ebe1869b059801e2107044.jpg");
        c14_1.setTime(Instant.parse("2025-08-04T11:00:00Z"));
        c14_1.setVote(Vote.REAL);
        c14_1.setNews(news14);

        Comment c14_2 = new Comment();
        c14_2.setUsername("Pierre Dpt");
        c14_2.setText("Brain-wave translation? Definitely skeptical.");
        c14_2.setImage(null);
        c14_2.setTime(Instant.parse("2025-08-04T14:00:00Z"));
        c14_2.setVote(Vote.FAKE);
        c14_2.setNews(news14);

        Comment c14_3 = new Comment();
        c14_3.setUsername("Javier López");
        c14_3.setText("Amazing tech, I can see this being possible.");
        c14_3.setImage(null);
        c14_3.setTime(Instant.parse("2025-08-04T15:00:00Z"));
        c14_3.setVote(Vote.REAL);
        c14_3.setNews(news14);

        news14.getComments().add(c14_1);
        news14.getComments().add(c14_2);
        news14.getComments().add(c14_3);
        newsDao.save(news14);

        // --- ข่าวที่ 15: Cancer Frequency Cure (FAKE) ---
        News news15 = new News();
        news15.setTopic("Breakthrough in Cancer Research Announced");
        news15.setShortDetail("Controversial scientist claims miracle frequency sound waves can cure any cancer in 72 hours, sparking fierce debate among medical professionals and desperate patients worldwide.");
        news15.setFullDetail("Dr. Marcus Heilmann, a self-proclaimed quantum oncologist operating from an undisclosed research facility, has announced what he calls the most significant medical breakthrough in human history - a revolutionary cancer treatment using specially calibrated sound frequencies that allegedly cure 100% of cancer cases within 72 hours without any side effects. According to Heilmann's unverified research, cancer cells vibrate at a specific death frequency of 528.7 Hz, and his proprietary HealWave technology can target and destroy malignant tumors by playing precisely tuned sound waves through modified headphones for just 20 minutes daily. The controversial treatment, which costs $75,000 per session and is only available at Heilmann's private clinic in an unnamed South American country, has reportedly attracted thousands of desperate patients despite fierce criticism from mainstream medical institutions. Dr. Sarah Johnson from the National Cancer Institute stated that there is absolutely no scientific evidence supporting frequency-based cancer treatment, and we're deeply concerned about patients abandoning proven therapies for unsubstantiated claims, while oncologist Dr. Michael Chen warned that this appears to be a dangerous exploitation of vulnerable cancer patients seeking miracle cures. Strangely, Heilmann claims to have cured over 5,000 patients but refuses to release medical records citing patient privacy laws, and several major medical journals have rejected his research papers for what peer reviewers describe as fundamental methodological flaws and impossible results. Despite mounting skepticism from the scientific community, testimonial videos featuring alleged cured patients continue circulating on social media, though investigations by health authorities have been unable to verify the identities or medical histories of any individuals featured in these promotional materials.");
        news15.setImage("https://d2ubrtwy6ww54e.cloudfront.net/www.uvmhealth.org/assets/styles/blog_hero/s3/2023-01/cancer-therapies-web.jpg");
        news15.setReporter("Luis Rodríguez");
        news15.setDateTime(Instant.parse("2025-08-05T15:00:00Z"));
        news15.setRealVotes(8);
        news15.setFakeVotes(20);
        newsDao.save(news15);

        // --- ข่าวที่ 16: Giant Sinkhole (FAKE) ---
        News news16 = new News();
        news16.setTopic("Giant Sinkhole Appears in Downtown Area");
        news16.setShortDetail("Mysterious 500-foot deep sinkhole opens in city center, revealing ancient underground civilization with glowing crystals and hieroglyphic writings that archaeologists claim could rewrite human history.");
        news16.setFullDetail("A massive sinkhole measuring 300 feet in diameter and an estimated 500 feet deep suddenly opened in the heart of downtown yesterday morning, swallowing three city blocks and exposing what city officials are calling an impossible archaeological discovery - the perfectly preserved remains of an advanced underground civilization complete with crystal-powered structures and walls covered in unidentified hieroglyphic symbols. Emergency response coordinator Captain James Mitchell, speaking from behind mysterious government-erected barriers that now surround the entire area, claimed that initial drone surveys revealed architectural elements that shouldn't exist according to our understanding of ancient technology, including what appears to be a functioning power grid made entirely of luminescent crystals that continue to emit a bright blue light. Dr. Elena Vasquez, a self-described xenoarchaeologist who was allegedly the first scientist lowered into the sinkhole, reported discovering tablets written in an unknown language that she believes may contain star maps pointing to Earth's original extraterrestrial visitors, though her findings have been immediately classified by federal authorities who arrived within hours of the discovery. Local geology professor Dr. Robert Kim expressed bewilderment, stating that geological surveys conducted just last month showed no underground cavities in this area, making this sinkhole's appearance scientifically impossible, while residents report strange electromagnetic interference affecting all electronic devices within a six-block radius since the hole appeared. Curiously, city officials have banned all media access to the site and are reportedly evacuating surrounding buildings under the pretense of structural safety concerns, leading conspiracy theorists to speculate that the government is covering up evidence of ancient alien contact rather than addressing what they claim is simply a routine geological phenomenon.");
        news16.setImage("https://media.wired.com/photos/59269d64cfe0d93c47430d6b/191:100/w_1280,c_limit/Before-623639136.jpg");
        news16.setReporter("Sarah Lee");
        news16.setDateTime(Instant.parse("2025-08-06T12:00:00Z"));
        news16.setRealVotes(13);
        news16.setFakeVotes(14);

        Comment c16_1 = new Comment();
        c16_1.setUsername("Anna Suda");
        c16_1.setText("This is absolutely mind-blowing! If the reports about glowing crystals and hieroglyphics are true, this could be the most important archaeological discovery in human history.");
        c16_1.setImage(null);
        c16_1.setTime(Instant.parse("2025-08-06T15:00:00Z"));
        c16_1.setVote(Vote.REAL);
        c16_1.setNews(news16);

        Comment c16_2 = new Comment();
        c16_2.setUsername("Michael Prasit");
        c16_2.setText("No geological evidence, no credible sources, and only mysterious 'classified findings.' Until there's hard proof, this is just another conspiracy theory.");
        c16_2.setImage("https://i.pinimg.com/1200x/86/bd/cc/86bdccac55661b840ac2050a3fe4c359.jpg");
        c16_2.setTime(Instant.parse("2025-08-06T17:00:00Z"));
        c16_2.setVote(Vote.FAKE);
        c16_2.setNews(news16);

        Comment c16_3 = new Comment();
        c16_3.setUsername("Benjamin Miller");
        c16_3.setText("I believe this. Governments always try to hide things like this, and the sudden media ban just makes it more convincing that they found something extraordinary underground.");
        c16_3.setImage("https://i.pinimg.com/736x/8e/47/ab/8e47abac1e9ac500a4ba17d9772408fd.jpg");
        c16_3.setTime(Instant.parse("2025-08-06T14:00:00Z"));
        c16_3.setVote(Vote.REAL);
        c16_3.setNews(news16);

        Comment c16_4 = new Comment();
        c16_4.setUsername("Mia Anderson");
        c16_4.setText("A 500-foot sinkhole revealing an 'ancient alien civilization' in the middle of a city? That sounds more like a sci-fi movie than real life.");
        c16_4.setImage(null);
        c16_4.setTime(Instant.parse("2025-08-06T16:00:00Z"));
        c16_4.setVote(Vote.FAKE);
        c16_4.setNews(news16);

        news16.getComments().add(c16_1);
        news16.getComments().add(c16_2);
        news16.getComments().add(c16_3);
        news16.getComments().add(c16_4);
        newsDao.save(news16);

        // --- ข่าวที่ 17: Tax Cut ---
        News news17 = new News();
        news17.setTopic("Government Announces Tax Cut for Small Businesses");
        news17.setShortDetail("Government unveils comprehensive small business tax relief package reducing corporate rates from 25% to 18%, while critics question funding sources and long-term economic impact.");
        news17.setFullDetail("The federal government announced a significant tax relief package for small businesses, reducing the corporate tax rate from 25% to 18% for companies with annual revenues under $2 million, a move that officials estimate will save qualifying businesses an average of $12,000 annually while potentially stimulating job creation and economic growth. Treasury Secretary Patricia Williams explained that the tax cuts, effective January 1st, are designed to help small enterprises recover from recent economic challenges and compete more effectively with larger corporations, though the initiative has sparked intense debate among economists and lawmakers about its fiscal implications. The proposal includes additional incentives such as expanded equipment depreciation allowances and simplified tax filing procedures, with the Small Business Administration projecting that approximately 850,000 businesses nationwide could benefit from the reduced rates. However, opposition leaders have raised concerns about the estimated $8.5 billion annual revenue loss to the federal budget, questioning how the government plans to offset the reduced income without cutting essential services or increasing the national deficit. Economic analyst Dr. Robert Chen from the Brookings Institution offered cautious optimism, noting that targeted tax relief can stimulate business investment and hiring, but the effectiveness depends largely on broader economic conditions and whether businesses actually use the savings for expansion rather than simply increasing profits. Meanwhile, small business owner Maria Rodriguez welcomed the news but expressed skepticism about the timing, stating that while tax cuts are always appreciated, many businesses are more concerned about supply chain disruptions and labor shortages that tax relief alone cannot address. The legislation still requires congressional approval, with heated debates expected over the coming weeks as lawmakers from both parties weigh the potential economic benefits against concerns about fiscal responsibility and equitable tax policy.");
        news17.setImage("https://assets.bwbx.io/images/users/iqjWHBFdfxIU/iNyEyqdV3pYY/v0/-1x-1.webp");
        news17.setReporter("John Miller");
        news17.setDateTime(Instant.parse("2025-08-07T16:00:00Z"));
        news17.setRealVotes(23);
        news17.setFakeVotes(23);

        Comment c17_1 = new Comment();
        c17_1.setUsername("Golf");
        c17_1.setText("This could be a real boost for small businesses. Cutting corporate taxes from 25% to 18% would definitely help struggling enterprises and stimulate the economy.");
        c17_1.setImage(null);
        c17_1.setTime(Instant.parse("2025-08-07T20:00:00Z"));
        c17_1.setVote(Vote.REAL);
        c17_1.setNews(news17);

        Comment c17_2 = new Comment();
        c17_2.setUsername("Mila Sanchez");
        c17_2.setText("I believe this is true. Governments often implement targeted tax relief programs, and the projected benefits for thousands of small businesses make sense.");
        c17_2.setImage(null);
        c17_2.setTime(Instant.parse("2025-08-07T21:00:00Z"));
        c17_2.setVote(Vote.REAL);
        c17_2.setNews(news17);

        Comment c17_3 = new Comment();
        c17_3.setUsername("Stella Rogers");
        c17_3.setText("An $8.5 billion budget hole? Sounds suspicious. Until Congress actually passes it and official documents are released, I'm skeptical this will happen.");
        c17_3.setImage("https://i.pinimg.com/736x/2c/5c/21/2c5c212e52ff6cd3d8b853758d54ba28.jpg");
        c17_3.setTime(Instant.parse("2025-08-07T20:00:00Z"));
        c17_3.setVote(Vote.FAKE);
        c17_3.setNews(news17);

        Comment c17_4 = new Comment();
        c17_4.setUsername("Aum");
        c17_4.setText("$8.5B lost? Seems fishy.");
        c17_4.setImage("https://i.pinimg.com/736x/29/e8/05/29e80518f21a781c1ddaf1479c298bf9.jpg");
        c17_4.setTime(Instant.parse("2025-08-07T19:00:00Z"));
        c17_4.setVote(Vote.FAKE);
        c17_4.setNews(news17);

        news17.getComments().add(c17_1);
        news17.getComments().add(c17_2);
        news17.getComments().add(c17_3);
        news17.getComments().add(c17_4);
        newsDao.save(news17);

        // --- ข่าวที่ 18: Holographic Phone ---
        News news18 = new News();
        news18.setTopic("New Smartphone Released with Holographic Display");
        news18.setShortDetail("Samsung unveils Galaxy Holo featuring advanced light field technology that projects 3D holographic images up to 6 inches above the screen, priced at $2,800 for early adopters.");
        news18.setFullDetail("Samsung has officially launched the Galaxy Holo, marking a significant milestone in mobile technology with the world's first commercially available smartphone featuring a functional holographic display system that uses advanced light field projection technology to create floating 3D images up to 6 inches above the device's surface. The breakthrough technology, developed in partnership with Israeli startup Looking Glass Factory, employs a proprietary array of micro-LEDs and precision optics to generate viewable holograms without requiring special glasses, though the effect is currently limited to a 45-degree viewing angle and works best in controlled lighting conditions. Samsung's head of mobile innovation, Dr. Lisa Park, demonstrated the device at Mobile World Congress, showing holographic video calls, 3D gaming experiences, and interactive product visualizations that respond to hand gestures detected by the phone's advanced depth sensors. The Galaxy Holo, priced at $2,800 for the 256GB model, represents a premium offering targeted at early technology adopters, content creators, and professionals in fields like architecture and medical imaging who could benefit from true 3D visualization capabilities. Technology analyst Ming-Chi Kuo noted that while the holographic display is genuinely impressive, current limitations including battery drain, viewing angle restrictions, and high production costs will likely keep this as a niche product until the technology matures. Early reviews from tech journalists who tested preview units report that the holographic effect works remarkably well for certain applications like 3D modeling and augmented reality experiences, though conventional smartphone tasks like reading text or browsing social media show little practical advantage over traditional displays. Samsung plans to release software development tools later this year to encourage app creators to explore holographic interfaces, while the company projects that refined versions of the technology could appear in mainstream devices within 3-5 years as manufacturing costs decrease and technical limitations are addressed.");
        news18.setImage("https://thedebrief.b-cdn.net/wp-content/uploads/2024/04/hologram.jpg");
        news18.setReporter("Laura White");
        news18.setDateTime(Instant.parse("2025-08-08T11:00:00Z"));
        news18.setRealVotes(29);
        news18.setFakeVotes(11);

        Comment c18_1 = new Comment();
        c18_1.setUsername("Hannah");
        c18_1.setText("Wow, a holographic phone? Looks legit.");
        c18_1.setImage("https://i.pinimg.com/736x/bb/71/23/bb7123aea24b79177c5a1602796fe70e.jpg");
        c18_1.setTime(Instant.parse("2025-08-08T12:00:00Z"));
        c18_1.setVote(Vote.REAL);
        c18_1.setNews(news18);

        Comment c18_2 = new Comment();
        c18_2.setUsername("Aurora Collins");
        c18_2.setText("This could actually be real tech, super cool!");
        c18_2.setImage(null);
        c18_2.setTime(Instant.parse("2025-08-08T13:00:00Z"));
        c18_2.setVote(Vote.REAL);
        c18_2.setNews(news18);

        Comment c18_3 = new Comment();
        c18_3.setUsername("Kevin Young");
        c18_3.setText("Exciting innovation! The price is steep, but if this tech is legit, it could set a new standard for mobile devices.");
        c18_3.setImage(null);
        c18_3.setTime(Instant.parse("2025-08-08T15:00:00Z"));
        c18_3.setVote(Vote.REAL);
        c18_3.setNews(news18);

        news18.getComments().add(c18_1);
        news18.getComments().add(c18_2);
        news18.getComments().add(c18_3);
        newsDao.save(news18);

        // --- ข่าวที่ 19: Renewable Energy Plan ---
        News news19 = new News();
        news19.setTopic("Government Launches New Renewable Energy Plan");
        news19.setShortDetail("The government launches a $50 billion renewable energy plan aiming for 50% clean electricity by 2030, including major solar and wind projects, consumer incentives up to $15,000, and creating 250,000 green jobs to establish the nation as a global clean energy leader.");
        news19.setFullDetail("The government has launched an ambitious $50 billion renewable energy initiative aimed at transforming the nation's power infrastructure, with the goal of generating 50% of electricity from renewable sources by 2030. This comprehensive plan includes the construction of 15 major solar farms and 20 offshore wind installations, alongside advanced energy storage systems and a $12 billion smart grid modernization program to enhance efficiency and reliability. Citizens will receive substantial benefits including rebates up to $15,000 for solar panel installations, zero-interest loans for energy upgrades, and $8,000 credits for electric vehicle purchases, while businesses can access enhanced tax incentives for clean energy investments. The initiative is projected to create 250,000 new jobs across engineering, manufacturing, and installation sectors, reduce household energy costs by 30%, and decrease national carbon emissions by 40% within five years. Energy Minister Sarah Chen emphasized that the plan represents more than climate action, stating it will secure energy independence and position the country as a global leader in clean energy technology, with quarterly progress reports ensuring full transparency and accountability throughout the implementation process.");
        news19.setImage("https://media.nationthailand.com/uploads/images/md/2024/06/wfnb7E2ihaF0IGs0Vk53.webp");
        news19.setReporter("Michael Lee");
        news19.setDateTime(Instant.parse("2025-08-14T16:27:45.406629Z"));
        news19.setRealVotes(150);
        news19.setFakeVotes(150);

        Comment c19_1 = new Comment();
        c19_1.setUsername("John Doe");
        c19_1.setText("Great step forward! 🌱 Very promising for the economy and environment.");
        c19_1.setImage("https://images.unsplash.com/photo-1654083198752-56ff209c8129?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1000&q=80");
        c19_1.setTime(Instant.parse("2025-08-14T10:30:00Z"));
        c19_1.setVote(Vote.REAL);
        c19_1.setNews(news19);

        Comment c19_2 = new Comment();
        c19_2.setUsername("Jane Smith");
        c19_2.setText("This could be a game changer for the environment. If they follow through, this plan would not only help with clean energy but also create thousands of jobs.");
        c19_2.setImage(null);
        c19_2.setTime(Instant.parse("2025-08-14T11:45:00Z"));
        c19_2.setVote(Vote.REAL);
        c19_2.setNews(news19);

        Comment c19_3 = new Comment();
        c19_3.setUsername("Fake News Watcher");
        c19_3.setText("I'm not buying it. A 50% renewable target by 2030 sounds too ambitious, especially with how slow the implementation has been in the past.");
        c19_3.setImage(null);
        c19_3.setTime(Instant.parse("2025-08-14T12:00:00Z"));
        c19_3.setVote(Vote.FAKE);
        c19_3.setNews(news19);

        news19.getComments().add(c19_1);
        news19.getComments().add(c19_2);
        news19.getComments().add(c19_3);
        newsDao.save(news19);

        // --- ข่าวที่ 20: Mystery Illness ---
        News news20 = new News();
        news20.setTopic("Mystery Illness Spreads in Rural Town");
        news20.setShortDetail("Over 200 residents in Millbrook have contracted an unknown respiratory illness, prompting CDC investigation into the mysterious pathogen causing fever and persistent cough.");
        news20.setFullDetail("The quiet farming community of Millbrook, population 3,500, has become the center of an intensive medical investigation after more than 200 residents developed a mysterious respiratory illness characterized by severe cough, high fever, and prolonged fatigue that doesn't match any known disease patterns. The outbreak began three weeks ago when several patients visited the local clinic with similar symptoms that failed to respond to standard treatments for flu, COVID-19, or bacterial infections. Dr. Elizabeth Warren, the town's chief medical officer, immediately contacted state health authorities when preliminary tests came back negative for all common respiratory pathogens. The Centers for Disease Control and Prevention has since deployed a rapid response team led by epidemiologist Dr. Michael Rodriguez, who established a temporary command center at Millbrook High School to coordinate the investigation and provide additional medical support. Environmental health specialists are examining potential sources including the town's water supply, a nearby agricultural processing plant, and recent changes in local farming practices, while virologists at the CDC's Atlanta laboratory are analyzing blood and tissue samples using advanced genetic sequencing techniques. Mayor Sarah Thompson has implemented voluntary isolation protocols and canceled all public gatherings as a precautionary measure, while assuring residents that food and medical supplies continue to arrive daily. The illness appears to have a recovery rate of 95% with most patients showing improvement after 10-14 days of supportive care, though health officials emphasize the importance of identifying the cause to prevent further spread to neighboring communities.");
        news20.setImage("https://i.guim.co.uk/img/media/ac19dc69e4510d4f2306f1450a097c5d95ea7a26/0_109_2500_1499/master/2500.jpg?width=1200&quality=85&auto=format&fit=max&s=3a5cdfed87e118dcdf0cd2ae924f99fd");
        news20.setReporter("John Miller");
        news20.setDateTime(Instant.parse("2025-08-10T16:00:00Z"));
        news20.setRealVotes(8);
        news20.setFakeVotes(14);

        Comment c20_1 = new Comment();
        c20_1.setUsername("Penelope Gonzalez");
        c20_1.setText("Hope the CDC finds the cause soon—this sounds serious.");
        c20_1.setImage(null);
        c20_1.setTime(Instant.parse("2025-08-10T21:00:00Z"));
        c20_1.setVote(Vote.REAL);
        c20_1.setNews(news20);

        Comment c20_2 = new Comment();
        c20_2.setUsername("Nok");
        c20_2.setText("200 people sick? Sounds exaggerated.");
        c20_2.setImage("https://i.pinimg.com/564x/03/9f/98/039f988ed60a27700294098950ff457f.jpg");
        c20_2.setTime(Instant.parse("2025-08-10T21:00:00Z"));
        c20_2.setVote(Vote.FAKE);
        c20_2.setNews(news20);

        Comment c20_3 = new Comment();
        c20_3.setUsername("Plaa");
        c20_3.setText("Could be panic or misinformation, I'm skeptical.");
        c20_3.setImage(null);
        c20_3.setTime(Instant.parse("2025-08-10T19:00:00Z"));
        c20_3.setVote(Vote.FAKE);
        c20_3.setNews(news20);

        Comment c20_4 = new Comment();
        c20_4.setUsername("Liu Qiang");
        c20_4.setText("Until CDC releases official statement, I won't believe it😡");
        c20_4.setImage(null);
        c20_4.setTime(Instant.parse("2025-08-10T21:00:00Z"));
        c20_4.setVote(Vote.FAKE);
        c20_4.setNews(news20);

        Comment c20_5 = new Comment();
        c20_5.setUsername("Jay");
        c20_5.setText("Could just be flu misreported, not really unknown.");
        c20_5.setImage(null);
        c20_5.setTime(Instant.parse("2025-08-10T20:00:00Z"));
        c20_5.setVote(Vote.FAKE);
        c20_5.setNews(news20);

        news20.getComments().add(c20_1);
        news20.getComments().add(c20_2);
        news20.getComments().add(c20_3);
        news20.getComments().add(c20_4);
        news20.getComments().add(c20_5);
        newsDao.save(news20);

        // --- ข่าวที่ 21: Solar Farm ---
        News news21 = new News();
        news21.setTopic("World's Largest Solar Farm Opens");
        news21.setShortDetail("The Al Dhafra Solar Farm in Abu Dhabi started operations, generating 2 gigawatts of clean energy to power 200,000 homes, marking a key milestone in renewable energy.");
        news21.setFullDetail("The Al Dhafra Solar Farm in Abu Dhabi has officially commenced operations as the world's largest single-site solar installation, spanning 20 square kilometers and generating 2 gigawatts of clean electricity capable of powering approximately 200,000 homes while reducing carbon emissions by 2.4 million tons annually. The $871 million project, developed through a partnership between Emirates Water and Electricity Company, Masdar, EDF Renewables, and Jinko Power, features over 4 million high-efficiency bifacial solar panels that utilize both direct sunlight and reflected light from the ground to maximize energy production. Crown Prince of Abu Dhabi Sheikh Khaled bin Mohamed bin Zayed Al Nahyan inaugurated the facility yesterday, emphasizing the UAE's commitment to achieving net-zero emissions by 2050 and positioning the nation as a global leader in renewable energy technology. The project employed over 4,000 workers during its three-year construction phase and represents a significant step toward the UAE's goal of generating 50% of its electricity from clean sources by 2050. International energy analysts predict that the farm's success will accelerate similar mega-projects worldwide, with the International Renewable Energy Agency noting that such large-scale installations are crucial for achieving global climate targets. The facility incorporates advanced tracking systems that follow the sun's movement throughout the day, increasing energy output by up to 20% compared to fixed installations, while also serving as a testing ground for next-generation photovoltaic technologies that could further revolutionize the solar industry.");
        news21.setImage("https://agreenerlifeagreenerworld.net/wp-content/uploads/2024/07/tarim-basin-solar-farm.-photo-credit-cfp.jpeg");
        news21.setReporter("Alice Johnson");
        news21.setDateTime(Instant.parse("2025-08-11T15:00:00Z"));
        news21.setRealVotes(13);
        news21.setFakeVotes(3);

        Comment c21_1 = new Comment();
        c21_1.setUsername("Nokky");
        c21_1.setText("Makes sense with all the solar tech advances.");
        c21_1.setImage(null);
        c21_1.setTime(Instant.parse("2025-08-11T16:00:00Z"));
        c21_1.setVote(Vote.REAL);
        c21_1.setNews(news21);

        Comment c21_2 = new Comment();
        c21_2.setUsername("Joy");
        c21_2.setText("Too perfect to be true, need proof.");
        c21_2.setImage("https://i.pinimg.com/736x/34/64/ad/3464ad1c33c983b87d66f14b092f11ee.jpg");
        c21_2.setTime(Instant.parse("2025-08-11T18:00:00Z"));
        c21_2.setVote(Vote.FAKE);
        c21_2.setNews(news21);

        news21.getComments().add(c21_1);
        news21.getComments().add(c21_2);
        newsDao.save(news21);

        // --- ข่าวที่ 22: Lab-Grown Meat ---
        News news22 = new News();
        news22.setTopic("Researchers Develop Lab-Grown Meat at Scale");
        news22.setShortDetail("Scientists at Netherlands-based Mosa Meat have achieved commercial-scale production of lab-grown beef, reducing costs by 90% and paving the way for widespread availability of cultivated meat in supermarkets by 2026.");
        news22.setFullDetail("Breakthrough research by Mosa Meat, the Netherlands-based biotechnology company founded by Dr. Mark Post, has successfully scaled up lab-grown beef production to industrial levels, achieving a dramatic 90% cost reduction that brings cultivated meat closer to price parity with conventional beef for the first time in the industry's history. The company's new 50,000-square-foot facility in Maastricht can produce 10,000 pounds of cultivated beef monthly using advanced bioreactor technology that grows animal cells in nutrient-rich media without the need for animal slaughter. Dr. Post, who created the world's first lab-grown burger in 2013, announced that production costs have dropped from $330,000 per pound to under $50 per pound through optimized cell lines, improved growth media formulations, and automated manufacturing processes. The European Food Safety Authority granted preliminary approval for the product last month, with full commercial authorization expected by early 2025, while the company has secured partnerships with major European retailers including Albert Heijn and Carrefour for distribution across 15 countries. Environmental scientists praise the development, noting that cultivated meat production requires 96% less land, 82% less water, and generates 78% fewer greenhouse gas emissions compared to conventional beef farming. Industry analysts project that the global cultivated meat market could reach $25 billion by 2030, with competing companies like Upside Foods and Good Meat racing to achieve similar production milestones as consumer acceptance grows and regulatory frameworks evolve worldwide.");
        news22.setImage("https://cdn.mos.cms.futurecdn.net/ZTP8d2zAcm5Yjz2VjNZJBM-1200-80.jpg");
        news22.setReporter("John Miller");
        news22.setDateTime(Instant.parse("2025-08-12T20:00:00Z"));
        news22.setRealVotes(19);
        news22.setFakeVotes(19);

        Comment c22_1 = new Comment();
        c22_1.setUsername("Oil");
        c22_1.setText("I'd like to see real supermarket availability first.");
        c22_1.setImage("https://i.pinimg.com/1200x/43/76/5a/43765acb57f02cb60b92d22c53ee1401.jpg");
        c22_1.setTime(Instant.parse("2025-08-13T00:00:00Z"));
        c22_1.setVote(Vote.FAKE);
        c22_1.setNews(news22);

        Comment c22_2 = new Comment();
        c22_2.setUsername("Jaruwan Boonmak");
        c22_2.setText("Could be marketing spin, not reality yet.");
        c22_2.setImage("https://i.pinimg.com/736x/3a/5c/91/3a5c9122e645b7c6b7f7f335779ee89e.jpg");
        c22_2.setTime(Instant.parse("2025-08-12T21:00:00Z"));
        c22_2.setVote(Vote.FAKE);
        c22_2.setNews(news22);

        Comment c22_3 = new Comment();
        c22_3.setUsername("Ethan White");
        c22_3.setText("Too perfect to be true, need verification.");
        c22_3.setImage("https://i.pinimg.com/736x/1b/96/c5/1b96c54cbd0339b11a7da86d65f06a6e.jpg");
        c22_3.setTime(Instant.parse("2025-08-13T00:00:00Z"));
        c22_3.setVote(Vote.FAKE);
        c22_3.setNews(news22);

        Comment c22_4 = new Comment();
        c22_4.setUsername("Worawit Promraksa");
        c22_4.setText("If experts say it's real, I trust this.");
        c22_4.setImage(null);
        c22_4.setTime(Instant.parse("2025-08-12T23:00:00Z"));
        c22_4.setVote(Vote.REAL);
        c22_4.setNews(news22);

        Comment c22_5 = new Comment();
        c22_5.setUsername("Kim Ji-hyun");
        c22_5.setText("This could really change the way we eat.");
        c22_5.setImage(null);
        c22_5.setTime(Instant.parse("2025-08-12T21:00:00Z"));
        c22_5.setVote(Vote.REAL);
        c22_5.setNews(news22);

        news22.getComments().add(c22_1);
        news22.getComments().add(c22_2);
        news22.getComments().add(c22_3);
        news22.getComments().add(c22_4);
        news22.getComments().add(c22_5);
        newsDao.save(news22);

        // --- ข่าวที่ 23: Data Breach ---
        News news23 = new News();
        news23.setTopic("Major Data Breach Affects Millions of Users");
        news23.setShortDetail("ConnectWorld revealed that hackers breached the personal data of 45 million users, including passwords and financial info, leading to security updates and a government investigation.");
        news23.setFullDetail("ConnectWorld, the popular social networking platform with over 200 million active users, announced yesterday that cybercriminals successfully breached their security systems and accessed sensitive personal information of approximately 45 million users between March 15-22, including usernames, email addresses, encrypted passwords, phone numbers, and stored payment card details for premium subscribers. The company's Chief Security Officer, Dr. Jennifer Martinez, revealed during an emergency press conference that the attack was discovered by their automated threat detection system on March 23, when unusual data transfer patterns triggered security alerts, leading to the immediate involvement of cybersecurity firm CyberShield and federal law enforcement agencies. The sophisticated breach appears to have exploited a previously unknown vulnerability in the platform's third-party authentication system, which hackers used to gain elevated access privileges and extract user data over a seven-day period before detection. ConnectWorld CEO Michael Thompson issued a formal apology and announced that all affected users have been automatically logged out and must reset their passwords, while the company has implemented additional multi-factor authentication requirements and upgraded their encryption protocols. The FBI's Cyber Crime Division, led by Special Agent Sarah Collins, has launched a full investigation and believes the attack may be linked to the international hacking group known as 'DataVault,' which has previously targeted major technology companies for financial gain. Cybersecurity experts warn that affected users should immediately change passwords on other accounts that may share similar credentials, monitor their financial statements for unauthorized transactions, and remain vigilant for phishing attempts as stolen data often appears on dark web marketplaces within weeks of major breaches.");
        news23.setImage("https://www.secpod.com/blog/wp-content/uploads/2020/06/data-breach.jpg");
        news23.setReporter("Sophia Martinez");
        news23.setDateTime(Instant.parse("2025-08-13T14:00:00Z"));
        news23.setRealVotes(19);
        news23.setFakeVotes(29);
        newsDao.save(news23);

        // --- ข่าวที่ 24: Drone Delivery (FAKE) ---
        News news24 = new News();
        news24.setTopic("Drone Delivery Service Expands Nationwide");
        news24.setShortDetail("SkyDelivery announced plans to launch drone delivery services in 50 major cities by year-end, sparking debate among aviation experts and local officials about airspace safety and regulatory oversight.");
        news24.setFullDetail("SkyDelivery, the innovative logistics company backed by venture capital firm TechVentures, unveiled ambitious plans yesterday to expand their autonomous drone delivery network to 50 major metropolitan areas across the United States by December 2025, representing the largest civilian drone operation in aviation history and igniting fierce debate among industry experts, federal regulators, and municipal authorities. CEO David Park announced that the company's fleet of 10,000 advanced hexacopter drones will provide 30-minute delivery of packages weighing up to 5 pounds within a 15-mile radius of designated distribution centers, utilizing artificial intelligence navigation systems and redundant safety protocols developed in partnership with aerospace manufacturer AeroDyne Technologies. The Federal Aviation Administration has granted conditional approval for the expansion under their new Commercial Drone Integration Program, though aviation safety expert Dr. Linda Harrison from the National Transportation Safety Board expressed concerns about potential mid-air collisions and the strain on already congested urban airspace. Local government officials remain divided on the initiative, with Seattle Mayor James Rodriguez praising the environmental benefits and job creation potential, while Phoenix City Council member Maria Santos cited noise pollution and privacy invasion as primary concerns requiring additional public hearings. The service promises to revolutionize e-commerce delivery for major retailers including QuickMart and GlobalShop, though critics argue that insufficient testing of the AI guidance systems in adverse weather conditions could pose significant risks to public safety. Transportation policy analyst Professor Robert Chen from Stanford University predicts that successful implementation could trigger a $50 billion transformation of the logistics industry within five years, while also necessitating comprehensive updates to federal aviation regulations and local zoning ordinances to accommodate the new aerial delivery infrastructure.");
        news24.setImage("https://s24806.pcdn.co/wp-content/uploads/2025/06/doordash-flytrex-delivery-970.jpg");
        news24.setReporter("Alice Johnson");
        news24.setDateTime(Instant.parse("2025-08-14T13:00:00Z"));
        news24.setRealVotes(2);
        news24.setFakeVotes(21);

        Comment c24_1 = new Comment();
        c24_1.setUsername("Maxie");
        c24_1.setText("Too ambitious, I'm skeptical.");
        c24_1.setImage("https://i.pinimg.com/1200x/24/17/24/241724215dde007280e57ec42e43793e.jpg");
        c24_1.setTime(Instant.parse("2025-08-14T18:00:00Z"));
        c24_1.setVote(Vote.FAKE);
        c24_1.setNews(news24);

        Comment c24_2 = new Comment();
        c24_2.setUsername("Jiraporn Boonying");
        c24_2.setText("Urban airspace is too crowded for this.");
        c24_2.setImage(null);
        c24_2.setTime(Instant.parse("2025-08-14T15:00:00Z"));
        c24_2.setVote(Vote.FAKE);
        c24_2.setNews(news24);

        Comment c24_3 = new Comment();
        c24_3.setUsername("Aronong Prasertsuk");
        c24_3.setText("Officials provide updates on the situation.");
        c24_3.setImage("https://i.pinimg.com/736x/f5/13/c1/f513c1879d645f57174e88034f5692a7.jpg");
        c24_3.setTime(Instant.parse("2025-08-14T17:00:00Z"));
        c24_3.setVote(Vote.REAL);
        c24_3.setNews(news24);

        Comment c24_4 = new Comment();
        c24_4.setUsername("Toon");
        c24_4.setText("Makes sense with current drone technology.");
        c24_4.setImage("https://i.pinimg.com/1200x/11/10/6c/11106ccb869afcab5bba203a23a4d896.jpg");
        c24_4.setTime(Instant.parse("2025-08-14T16:00:00Z"));
        c24_4.setVote(Vote.REAL);
        c24_4.setNews(news24);

        Comment c24_5 = new Comment();
        c24_5.setUsername("Joshua Nelson");
        c24_5.setText("If this works as promised, it could revolutionize the delivery industry. The potential for fast and efficient deliveries is huge, but airspace safety will be key.");
        c24_5.setImage(null);
        c24_5.setTime(Instant.parse("2025-08-14T17:00:00Z"));
        c24_5.setVote(Vote.REAL);
        c24_5.setNews(news24);

        news24.getComments().add(c24_1);
        news24.getComments().add(c24_2);
        news24.getComments().add(c24_3);
        news24.getComments().add(c24_4);
        news24.getComments().add(c24_5);
        newsDao.save(news24);

        // --- ข่าวที่ 25: Heatwave ---
        News news25 = new News();
        news25.setTopic("Unprecedented Heatwave Hits Northern Europe");
        news25.setShortDetail("Northern Europe faces record temperatures of 45°C, causing power outages and emergency cooling centers as residents struggle with the extreme heat.");
        news25.setFullDetail("An extraordinary heatwave has engulfed Northern Europe for the past week, with temperatures soaring to unprecedented levels of 47°C in Stockholm, 45°C in Helsinki, and 44°C in Copenhagen, shattering century-old weather records and forcing governments across the region to declare national heat emergencies. The extreme weather event, attributed by meteorologists to a persistent high-pressure system combined with climate change effects, has overwhelmed electrical grids as air conditioning usage peaked, causing rolling blackouts in major cities and prompting utility companies to implement emergency power rationing measures. Swedish Prime Minister Erik Lundberg announced the opening of 200 emergency cooling centers in schools and community buildings, while Norwegian authorities have distributed over 500,000 free fans to elderly residents most vulnerable to heat-related illness. Local resident Maria Johansson from Stockholm expressed shock at the conditions, stating that her apartment reached 38°C despite closed blinds and fans, while Helsinki resident Pekka Virtanen reported that his usual morning jog became impossible due to the oppressive heat. Climate scientist Dr. Anna Bergström from the University of Copenhagen warned that this heatwave represents a dangerous preview of future summer conditions in the region, with computer models suggesting similar events could become annual occurrences by 2035. Emergency medical services have reported a 300% increase in heat-related hospitalizations, particularly among the elderly and those with pre-existing conditions, while agricultural experts estimate that the extreme temperatures have damaged 40% of regional crop yields, potentially leading to food shortages. The European Centre for Medium-Range Weather Forecasts predicts the heatwave will persist for at least another ten days, prompting calls from environmental activists for immediate action on carbon emissions and infrastructure adaptation to handle increasingly severe weather patterns.");
        news25.setImage("https://www.ecowatch.com/wp-content/uploads/2021/10/1706207636-origin.jpg");
        news25.setReporter("David Brown");
        news25.setDateTime(Instant.parse("2025-08-15T16:00:00Z"));
        news25.setRealVotes(19);
        news25.setFakeVotes(23);

        Comment c25_1 = new Comment();
        c25_1.setUsername("Joseph Moore");
        c25_1.setText("This sounds like media fear-mongering. Sure, it's hot, but I'm not buying the idea that it's the worst heatwave ever. They always say that.");
        c25_1.setImage(null);
        c25_1.setTime(Instant.parse("2025-08-15T19:00:00Z"));
        c25_1.setVote(Vote.FAKE);
        c25_1.setNews(news25);

        Comment c25_2 = new Comment();
        c25_2.setUsername("Felix K.J.");
        c25_2.setText("This is terrifying. With temperatures hitting these levels, it's clear that climate change is having a serious impact. We need to take action before it's too late.");
        c25_2.setImage("https://i.pinimg.com/736x/56/15/20/561520db0c3626e181b5768426efe263.jpg");
        c25_2.setTime(Instant.parse("2025-08-15T19:00:00Z"));
        c25_2.setVote(Vote.REAL);
        c25_2.setNews(news25);

        Comment c25_3 = new Comment();
        c25_3.setUsername("Stella Carpenter");
        c25_3.setText("A heatwave this extreme in Northern Europe? Seems like another exaggeration. How could they not have prepared for something like this?");
        c25_3.setImage(null);
        c25_3.setTime(Instant.parse("2025-08-15T18:00:00Z"));
        c25_3.setVote(Vote.FAKE);
        c25_3.setNews(news25);

        news25.getComments().add(c25_1);
        news25.getComments().add(c25_2);
        news25.getComments().add(c25_3);
        newsDao.save(news25);

        // --- ข่าวที่ 26: Battery Technology ---
        News news26 = new News();
        news26.setTopic("Breakthrough Battery Technology Promises 5-Minute Charge");
        news26.setShortDetail("TechPower Labs unveiled solid-state batteries that charge electric vehicles in 5 minutes and last 1 million miles, potentially transforming the automotive industry.");
        news26.setFullDetail("TechPower Labs announced a revolutionary breakthrough in solid-state battery technology that enables electric vehicles to achieve full charge in just 5 minutes while maintaining battery life for over 1 million miles of driving. The new PowerCell-X batteries utilize proprietary silicon nanowire anodes and ceramic electrolytes developed by Dr. Rachel Kim's team, achieving energy density 300% higher than current lithium-ion batteries while eliminating fire risks. CEO Mark Stevens demonstrated the technology by charging a Tesla Model S equivalent battery pack from 0% to 100% in 4 minutes and 47 seconds using their prototype ultra-fast charging station. Major automakers including Ford and General Motors have expressed significant interest, with Ford signing a preliminary $12 billion supply agreement for integration into their 2027 vehicle lineup. However, industry analyst Dr. Jennifer Martinez cautioned that mass production challenges and charging infrastructure costs could delay widespread implementation by 3-5 years. Energy Secretary Amanda Roberts praised the innovation as crucial for achieving 50% EV market share by 2030, while TechPower Labs plans to begin pilot production next year with initial capacity of 100,000 battery packs annually.");
        news26.setImage("https://carnewschina.com/wp-content/uploads/2025/04/768d4066b04f4dc7bc58c3e504f05f4d.png");
        news26.setReporter("Zhang Wei");
        news26.setDateTime(Instant.parse("2025-08-16T08:00:00Z"));
        news26.setRealVotes(2);
        news26.setFakeVotes(5);

        Comment c26_1 = new Comment();
        c26_1.setUsername("Harper Turner");
        c26_1.setText("I'm skeptical. This is probably just another overhyped announcement. Even if it's true, it will take years before it's available for everyday use.");
        c26_1.setImage("https://i.pinimg.com/736x/ec/ec/d1/ececd1a7e07d5dce6eeadd66d3b6faeb.jpg");
        c26_1.setTime(Instant.parse("2025-08-16T13:00:00Z"));
        c26_1.setVote(Vote.FAKE);
        c26_1.setNews(news26);

        Comment c26_2 = new Comment();
        c26_2.setUsername("Oliver Harris");
        c26_2.setText("This technology sounds promising. If it lives up to its claims, it could really transform how we think about electric cars and energy storage.");
        c26_2.setImage("https://image.made-in-china.com/251f0j00UYfGuGRPdEVh/made-in-china.jpg");
        c26_2.setTime(Instant.parse("2025-08-16T11:00:00Z"));
        c26_2.setVote(Vote.REAL);
        c26_2.setNews(news26);

        news26.getComments().add(c26_1);
        news26.getComments().add(c26_2);
        newsDao.save(news26);

        // --- ข่าวที่ 27: Landmark Restoration ---
        News news27 = new News();
        news27.setTopic("Famous Landmark to Undergo Restoration");
        news27.setShortDetail("The Statue of Liberty faces a controversial $500 million restoration using modern materials, sparking debate about preserving authenticity versus structural integrity.");
        news27.setFullDetail("The National Park Service announced a comprehensive $500 million restoration of the Statue of Liberty, involving replacement of deteriorating sections with advanced composite materials to combat climate change effects, sparking debate among preservation experts about historical authenticity versus structural stability. The five-year project, led by restoration architect Dr. Maria Gonzalez, will replace the statue's corroding iron framework with lightweight titanium alloy supports while maintaining the copper exterior through advanced patina-matching techniques. Park Service Director James Mitchell defended the approach, citing engineering reports showing the 139-year-old landmark faces structural failure within a decade due to rising sea levels, storms, and pollution. Art historian Professor Catherine Wells from Columbia University criticized the plan, arguing that replacing original materials fundamentally alters historical integrity and sets dangerous precedents for cultural artifacts. The French government has requested involvement through their Ministry of Culture, expressing concerns about maintaining symbolic authenticity while acknowledging preservation necessity. Tourism representatives estimate extended closure could cost New York City over $2 billion in lost revenue, while environmental groups praise climate adaptation features. Public polls show Americans evenly divided, with 48% supporting modern preservation techniques and 47% preferring traditional methods despite potentially reduced effectiveness against environmental threats.");
        news27.setImage("https://www.telegraph.co.uk/content/dam/Travel/2018/June/sphinx-GettyImages-643614006.jpg?imwidth=640");
        news27.setReporter("Emily Davis");
        news27.setDateTime(Instant.parse("2025-08-17T14:00:00Z"));
        news27.setRealVotes(5);
        news27.setFakeVotes(15);

        Comment c27_1 = new Comment();
        c27_1.setUsername("Li Ting");
        c27_1.setText("How convenient! Replacing the statue's original materials with modern ones? Feels like they're ruining history in the name of 'preservation.");
        c27_1.setImage(null);
        c27_1.setTime(Instant.parse("2025-08-17T15:00:00Z"));
        c27_1.setVote(Vote.FAKE);
        c27_1.setNews(news27);

        Comment c27_2 = new Comment();
        c27_2.setUsername("Paulo Santos");
        c27_2.setText("I find it hard to believe that a statue that's lasted for 139 years suddenly needs such drastic changes. This sounds like an excuse for corporate interests.");
        c27_2.setImage(null);
        c27_2.setTime(Instant.parse("2025-08-17T19:00:00Z"));
        c27_2.setVote(Vote.FAKE);
        c27_2.setNews(news27);

        Comment c27_3 = new Comment();
        c27_3.setUsername("Suwanna n.");
        c27_3.setText("I'm all for preserving history, but replacing original parts with new materials feels like changing what made the statue iconic. What's next, a complete overhaul?");
        c27_3.setImage(null);
        c27_3.setTime(Instant.parse("2025-08-17T15:00:00Z"));
        c27_3.setVote(Vote.FAKE);
        c27_3.setNews(news27);

        Comment c27_4 = new Comment();
        c27_4.setUsername("João Silva");
        c27_4.setText("This project sounds like a disaster. If it's not broken, why fix it? The Statue of Liberty has stood the test of time without all these modern fixes.");
        c27_4.setImage("https://i.pinimg.com/736x/18/f4/70/18f470dcba6c8eff8326060bc6215c50.jpg");
        c27_4.setTime(Instant.parse("2025-08-17T15:00:00Z"));
        c27_4.setVote(Vote.FAKE);
        c27_4.setNews(news27);

        news27.getComments().add(c27_1);
        news27.getComments().add(c27_2);
        news27.getComments().add(c27_3);
        news27.getComments().add(c27_4);
        newsDao.save(news27);

        // --- ข่าวที่ 28: Ancient City ---
        News news28 = new News();
        news28.setTopic("Archaeologists Unearth Ancient City");
        news28.setShortDetail("Archaeologists discovered a 4,000-year-old Mesopotamian city in Iraq with advanced infrastructure and cuneiform tablets that could rewrite ancient history.");
        news28.setFullDetail("An international team led by Dr. Sarah Johnson from Oxford University has uncovered a remarkably preserved 4,000-year-old Mesopotamian city near Baghdad, featuring sophisticated urban infrastructure including paved roads, drainage systems, and multi-story buildings that challenge assumptions about Bronze Age capabilities. The 300-acre site contains over 15,000 cuneiform tablets providing unprecedented insights into ancient trade networks, legal systems, and daily life during the Babylonian Empire. Initial translations by Professor Michael Chen reveal previously unknown trade routes connecting Mesopotamia to the Indus Valley, suggesting more extensive ancient global commerce than historians believed. Extraordinary artifacts include intricately carved cylinder seals, advanced bronze weapons, and ceramics with astronomical observations demonstrating sophisticated mathematical knowledge. Iraqi Minister of Culture Dr. Layla Al-Rashid called it potentially the most significant regional archaeological find since the 1920s, while UNESCO announced emergency funding for site protection. The research team, collaborating with Iraqi antiquities authorities, plans a permanent research station with digital preservation technology to create 3D models of structures and artifacts. Preliminary analysis suggests the city was suddenly abandoned around 1800 BCE, possibly due to climate change or invasion, with residents leaving behind valuable possessions and documents, making this a unique time capsule of ancient Mesopotamian civilization.");
        news28.setImage("https://idsb.tmgrup.com.tr/ly/uploads/images/2021/04/10/106838.jpg");
        news28.setReporter("Yusuf Ibrahim");
        news28.setDateTime(Instant.parse("2025-08-18T17:00:00Z"));
        news28.setRealVotes(27);
        news28.setFakeVotes(11);

        Comment c28_1 = new Comment();
        c28_1.setUsername("Grace Williams");
        c28_1.setText("Incredible! I've always thought there's so much more to ancient civilizations than what we've been taught. This find proves it.");
        c28_1.setImage(null);
        c28_1.setTime(Instant.parse("2025-08-18T18:00:00Z"));
        c28_1.setVote(Vote.REAL);
        c28_1.setNews(news28);

        Comment c28_2 = new Comment();
        c28_2.setUsername("Daniel T.");
        c28_2.setText("This sounds like a publicity stunt. How could they have missed such an important site for so long?");
        c28_2.setImage(null);
        c28_2.setTime(Instant.parse("2025-08-18T20:00:00Z"));
        c28_2.setVote(Vote.FAKE);
        c28_2.setNews(news28);

        Comment c28_3 = new Comment();
        c28_3.setUsername("Mason Lewis");
        c28_3.setText("I'm not buying this. Every time they 'unearth' something amazing, it ends up being exaggerated or debunked later.");
        c28_3.setImage(null);
        c28_3.setTime(Instant.parse("2025-08-18T18:00:00Z"));
        c28_3.setVote(Vote.FAKE);
        c28_3.setNews(news28);

        news28.getComments().add(c28_1);
        news28.getComments().add(c28_2);
        news28.getComments().add(c28_3);
        newsDao.save(news28);

        // --- ข่าวที่ 29: Self-Driving Taxis ---
        News news29 = new News();
        news29.setTopic("Self-Driving Taxis Begin Service in Capital");
        news29.setShortDetail("Autonomous taxi service RoboRide launched in Washington D.C. with 200 driverless vehicles, marking the first fully automated taxi fleet in a major U.S. capital city.");
        news29.setFullDetail("RoboRide, the San Francisco-based autonomous vehicle company, officially launched the first fully driverless taxi service in Washington D.C. yesterday with a fleet of 200 electric vehicles operating 24/7 across downtown and surrounding neighborhoods, representing a historic milestone in urban transportation and sparking both excitement and safety concerns among residents and officials. The sleek white vehicles, equipped with advanced LiDAR sensors, cameras, and AI navigation systems developed over eight years of testing, can be summoned through a smartphone app and operate without human safety drivers for the first time in the nation's capital. D.C. Mayor Patricia Williams celebrated the launch at a ribbon-cutting ceremony, emphasizing the potential for reduced traffic congestion and improved mobility for disabled residents, while acknowledging ongoing discussions with labor unions representing traditional taxi drivers who fear job displacement. The service initially covers a 25-square-mile area including Capitol Hill, downtown, and Georgetown, with plans to expand citywide by 2026 pending regulatory approval and public acceptance. However, transportation safety advocate Dr. Robert Martinez from the Highway Safety Institute expressed concerns about the vehicles' ability to handle unpredictable situations like construction zones and emergency vehicles, citing recent incidents in other test cities. Federal regulators from the National Highway Traffic Safety Administration are closely monitoring the deployment, with Administrator Jennifer Park stating that comprehensive data collection will inform future autonomous vehicle policies nationwide. Early user reviews have been mixed, with commuters praising the smooth rides and competitive pricing at $0.85 per mile, while others report confusion about pickup locations and concerns about riding in vehicles without human operators during late-night hours.");
        news29.setImage("https://eu-images.contentstack.com/v3/assets/blt31d6b0704ba96e9d/blt771dc5c2e03d8c80/667df77fe96baf6a6960ff58/pic_5.jpg?width=1280&auto=webp&quality=80&format=jpg&disable=upscale");
        news29.setReporter("Isabelle Robert");
        news29.setDateTime(Instant.parse("2025-08-19T20:00:00Z"));
        news29.setRealVotes(2);
        news29.setFakeVotes(19);

        Comment c29_1 = new Comment();
        c29_1.setUsername("Hubleaw no five");
        c29_1.setText("I'm skeptical about this. Driverless taxis have been announced many times before and failed to take off. Let's see how long this lasts.");
        c29_1.setImage("https://i.pinimg.com/736x/68/00/48/68004889c41f09fa8fc06bdfc37b03ef.jpg");
        c29_1.setTime(Instant.parse("2025-08-20T00:00:00Z"));
        c29_1.setVote(Vote.FAKE);
        c29_1.setNews(news29);

        Comment c29_2 = new Comment();
        c29_2.setUsername("Maria Bianchi");
        c29_2.setText("I've been waiting for this for years! It's great to see driverless taxis finally becoming a reality in a major city.");
        c29_2.setImage(null);
        c29_2.setTime(Instant.parse("2025-08-19T22:00:00Z"));
        c29_2.setVote(Vote.REAL);
        c29_2.setNews(news29);

        Comment c29_3 = new Comment();
        c29_3.setUsername("Marta Ruiz");
        c29_3.setText("Driverless cars in a major city? Feels like they're jumping the gun. How can we trust this technology when it's still so new?");
        c29_3.setImage(null);
        c29_3.setTime(Instant.parse("2025-08-19T22:00:00Z"));
        c29_3.setVote(Vote.FAKE);
        c29_3.setNews(news29);

        news29.getComments().add(c29_1);
        news29.getComments().add(c29_2);
        news29.getComments().add(c29_3);
        newsDao.save(news29);

        // --- ข่าวที่ 30: Rare Animal ---
        News news30 = new News();
        news30.setTopic("Rare Animal Species Spotted After Decades");
        news30.setShortDetail("The Javan elephant, believed extinct for 40 years, was photographed by camera traps in Indonesia's remote forests, offering new hope for conservation efforts.");
        news30.setFullDetail("Wildlife researchers from the Indonesian Institute of Sciences have confirmed the existence of a small population of Javan elephants in the remote forests of West Java, marking the first verified sighting of the species in over four decades after it was presumed extinct due to deforestation and human encroachment. The breakthrough discovery came through motion-activated camera traps installed by Dr. Sari Indrawati's conservation team, which captured clear footage of at least six individuals including a mother with her calf foraging near a hidden water source in Gunung Halimun-Salak National Park. The Javan elephant, a subspecies distinct from Sumatran and Asian elephants, was last officially documented in 1982 before habitat destruction eliminated their known populations, making this rediscovery one of the most significant conservation stories of the decade. Indonesian Environment Minister Dr. Ahmad Sutrisno announced immediate emergency protection measures for the estimated 12-15 remaining individuals, including expanded patrol units and strict access restrictions to their habitat area. International elephant expert Dr. Lisa Thompson from the World Wildlife Fund called the discovery miraculous but emphasized the species remains critically endangered, requiring urgent genetic diversity studies and potential breeding programs to ensure long-term survival. Local communities in nearby villages reported occasional signs of large animals over the years but dismissed them as Sumatran elephants migrating from other regions, until DNA analysis of dung samples confirmed the unique genetic markers of the Javan subspecies. The Indonesian government has pledged $5 million in emergency conservation funding while collaborating with international organizations to develop a comprehensive species recovery plan, though experts warn that habitat protection and human-wildlife conflict mitigation will be crucial for preventing another near-extinction event.");
        news30.setImage("https://assets.globalwildlife.org/m/7550306bdae0c311/webimage-Rediscovered-Fernandina-Giant-Tortoise.jpg");
        news30.setReporter("Michael Smith");
        news30.setDateTime(Instant.parse("2025-08-20T20:00:00Z"));
        news30.setRealVotes(6);
        news30.setFakeVotes(4);

        Comment c30_1 = new Comment();
        c30_1.setUsername("Peter Weber");
        c30_1.setText("This sounds too good to be true. The Javan elephant has been gone for decades, and now it's suddenly 'discovered'? Doubtful.");
        c30_1.setImage("https://i.pinimg.com/736x/34/64/ad/3464ad1c33c983b87d66f14b092f11ee.jpg");
        c30_1.setTime(Instant.parse("2025-08-20T21:00:00Z"));
        c30_1.setVote(Vote.FAKE);
        c30_1.setNews(news30);

        Comment c30_2 = new Comment();
        c30_2.setUsername("Sato Shota");
        c30_2.setText("This sounds too convenient. How did they miss these elephants all this time? I doubt this will last.");
        c30_2.setImage(null);
        c30_2.setTime(Instant.parse("2025-08-21T00:00:00Z"));
        c30_2.setVote(Vote.FAKE);
        c30_2.setNews(news30);

        news30.getComments().add(c30_1);
        news30.getComments().add(c30_2);
        newsDao.save(news30);

        // --- ข่าวที่ 31: Moo Deng ---
        News news31 = new News();
        news31.setTopic("Here We Go! 'Moo Deng' Pygmy Hippo Meme Valued at Nearly 150 Million USD");
        news31.setShortDetail("Moo Deng, a viral pygmy hippo from Khao Kiew Zoo, boosted zoo visits and appeared in ads. No royalties have been paid to the zoo. @hippo_cto recently donated 5 million THB, raising the coin's market cap to 143 million USD.");
        news31.setFullDetail("Moo Deng, a 2-month-old pygmy hippo from Khao Kiew Zoo, became a viral sensation worldwide after its cute pictures and videos were shared on the Khamoo & The Gang Facebook page. The page originally posted adorable moments of animals at the zoo, such as capybaras and older hippos, which led to Moo Deng gaining widespread attention and helping the zoo attract over 100,000 visitors per month. This popularity turned Moo Deng into a social media superstar, beloved by fans of all ages. Although Moo Deng's fame led to brands using its image in various ads and merchandise, such as t-shirts, bags, and billboards, there has been no news about any brand paying royalties to Khao Kiew Zoo. This raises questions about how the profits from this fame are shared. Moo Deng is not only a cute animal but has also become a valuable asset with significant economic impact. Recently, @hippo_cto, a cryptocurrency organization, donated 5 million THB to Khao Kiew Zoo, which boosted the market cap of the associated coin to 143 million USD. This highlights the financial worth of Moo Deng as not just a viral sensation but also a key player in the digital economy. However, questions about how copyrights and benefits from this fame are managed remain to be addressed, ensuring that Moo Deng's caregivers and Khao Kiew Zoo receive fair compensation for the viral success they've helped create.");
        news31.setImage("https://files-world.thaipbs.or.th/16_9_d662e87653.png");
        news31.setReporter("Nina Wong");
        news31.setDateTime(Instant.parse("2025-08-09T11:00:00Z"));
        news31.setRealVotes(46);
        news31.setFakeVotes(46);

        Comment c31_1 = new Comment();
        c31_1.setUsername("Kaem W.");
        c31_1.setText("It's so cute.");
        c31_1.setImage("https://s.isanook.com/jo/0/ud/494/2474469/m11.jpg?ip/resize/w728/q80/jpg");
        c31_1.setTime(Instant.parse("2025-08-09T13:00:00Z"));
        c31_1.setVote(Vote.REAL);
        c31_1.setNews(news31);

        Comment c31_2 = new Comment();
        c31_2.setUsername("Mia Choi");
        c31_2.setText("Seems like Moo Deng's fame is being used for profit without fair compensation to the zoo. Kinda sketchy.");
        c31_2.setImage(null);
        c31_2.setTime(Instant.parse("2025-08-09T14:00:00Z"));
        c31_2.setVote(Vote.FAKE);
        c31_2.setNews(news31);

        Comment c31_3 = new Comment();
        c31_3.setUsername("Liam Phan");
        c31_3.setText("Moo Deng is a star! But the zoo should definitely get paid for the brand deals.");
        c31_3.setImage("https://www.mp-uni.com/th/wp-content/uploads/sites/7/2024/09/hippo-moo-deng-01.jpg");
        c31_3.setTime(Instant.parse("2025-08-09T15:00:00Z"));
        c31_3.setVote(Vote.REAL);
        c31_3.setNews(news31);

        Comment c31_4 = new Comment();
        c31_4.setUsername("Sophie Li");
        c31_4.setText("It's just a hippo, though.");
        c31_4.setImage(null);
        c31_4.setTime(Instant.parse("2025-08-09T16:00:00Z"));
        c31_4.setVote(Vote.FAKE);
        c31_4.setNews(news31);

        news31.getComments().add(c31_1);
        news31.getComments().add(c31_2);
        news31.getComments().add(c31_3);
        news31.getComments().add(c31_4);
        newsDao.save(news31);

    }

    private void initDefaultUsers() {
        seedUserIfMissing(
                "admin",
                "admin@example.com",
                "System",
                "Admin",
                "admin",
                Role.ROLE_ADMIN
        );
        seedUserIfMissing(
                "member",
                "member@example.com",
                "Regular",
                "Member",
                "member",
                Role.ROLE_MEMBER
        );
        seedUserIfMissing(
                "reader",
                "reader@example.com",
                "Casual",
                "Reader",
                "reader",
                Role.ROLE_READER
        );
    }

    private void seedUserIfMissing(
            String username,
            String email,
            String firstname,
            String lastname,
            String rawPassword,
            Role role
    ) {
        Optional<User> existing = userRepository.findByUsername(username);
        if (existing.isPresent()) {
            return;
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .firstname(firstname)
                .lastname(lastname)
                .password(passwordEncoder.encode(rawPassword))
                .roles(List.of(role))
                .enabled(true)
                .build();

        userRepository.save(user);
    }
}
