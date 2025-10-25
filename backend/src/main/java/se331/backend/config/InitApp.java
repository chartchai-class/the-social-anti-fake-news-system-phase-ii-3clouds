package se331.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import se331.backend.dao.NewsDao;
import se331.backend.entity.Comment;
import se331.backend.entity.News;
import se331.backend.entity.Vote;

import java.time.Instant;

@Component
public class InitApp implements CommandLineRunner {

    @Autowired
    private NewsDao newsDao; // Inject DAO เพื่อใช้บันทึกข้อมูล

    @Override
    @Transactional // ใช้ @Transactional เพื่อให้จัดการ session ของ Hibernate ได้ถูกต้อง
    public void run(String... args) throws Exception {

        // ตรวจสอบว่ามีข้อมูลอยู่แล้วหรือยัง ถ้ายังไม่มี ค่อยเพิ่ม
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

        // คอมเมนต์สำหรับข่าวที่ 1
        Comment c1_1 = new Comment();
        c1_1.setUsername("Jack Phillips");
        c1_1.setText("Feels too good to be true, I’m skeptical.");
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
        c1_3.setText("Until independent reports confirm, I won’t fully trust this.");
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

        // บันทึกข่าวที่ 1 (Comment จะถูกบันทึกไปด้วย)
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

        // คอมเมนต์สำหรับข่าวที่ 2
        Comment c2_1 = new Comment();
        c2_1.setUsername("Guillaume Leclerc");
        c2_1.setText("Wow! This could change how we think about habitable planets. 🚀");
        c2_1.setImage("https://www.universetoday.com/article_images/helix-nebula.jpeg");
        c2_1.setTime(Instant.parse("2025-08-14T13:15:00Z"));
        c2_1.setVote(Vote.REAL);
        c2_1.setNews(news2);

        Comment c2_2 = new Comment();
        c2_2.setUsername("Su young");
        c2_2.setText("Another Earth-like planet? We’ve seen this before, nothing new.");
        c2_2.setImage(null);
        c2_2.setTime(Instant.parse("2025-08-14T13:50:00Z"));
        c2_2.setVote(Vote.FAKE);
        c2_2.setNews(news2);

        news2.getComments().add(c2_1);
        news2.getComments().add(c2_2);

        // บันทึกข่าวที่ 2
        newsDao.save(news2);

        System.out.println("Sample data initialization complete. (2 new articles added)");
    }
}