package se331.backend.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import se331.backend.entity.News;
import se331.backend.repository.NewsRepository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * DAO = Data Access Object (ชั้นที่ติดต่อกับฐานข้อมูล)
 * - รับคำสั่งจาก Service
 * - ไปดึง/บันทึกข้อมูลจาก Database ผ่าน Repository
 * - ประมวลผลข้อมูล (เช่น กรอง, คำนวณสถานะ)
 * - ส่งข้อมูลกลับไปให้ Service
 */
@Repository
public class NewsDaoImpl implements NewsDao {

    @Autowired
    private NewsRepository newsRepository; // ตัวติดต่อกับ database จริงๆ

    // ========================================
    // CRUD พื้นฐาน (Create, Read, Update, Delete)
    // ========================================

    @Override
    public List<News> findAll() {
        // ดึงข่าวทั้งหมดจาก database
        return newsRepository.findAll();
    }

    @Override
    public Optional<News> findById(Long id) {
        // ค้นหาข่าวตาม ID (Optional = อาจจะเจอหรือไม่เจอก็ได้)
        return newsRepository.findById(id);
    }

    @Override
    public News save(News news) {
        // บันทึกข่าว (ถ้ามี ID แล้ว = update, ถ้ายังไม่มี = create)
        return newsRepository.save(news);
    }

    @Override
    public void deleteById(Long id) {
        // ลบข่าวออกจาก database
        newsRepository.deleteById(id);
    }

    // ========================================
    // สำหรับ Normal Users (ปกติที่ login มา)
    // ไม่แสดงข่าวที่ removed = true
    // ========================================

    /**
     * ค้นหาข่าวด้วยคำค้น (keyword)
     * วิธีทำงาน:
     * 1. ค้นหา keyword ใน 3 ฟิลด์: topic, shortDetail, reporter
     * 2. รวมผลลัพธ์ทั้งหมด (ไม่ให้ซ้ำ)
     * 3. แบ่งหน้าตาม pageable
     */
    @Override
    public Page<News> searchByKeyword(String keyword, Pageable pageable) {
        // ใช้ Set เพื่อเก็บผลลัพธ์ไม่ให้ซ้ำ
        // LinkedHashSet = เก็บลำดับการเพิ่มไว้ (เจอใน topic ก่อนจะแสดงก่อน)
        Set<News> results = new LinkedHashSet<>();

        // ค้นหาใน topic (หัวข้อข่าว)
        // findByRemovedFalseAnd... = หาเฉพาะข่าวที่ไม่ถูกลบ (removed = false)
        // ContainingIgnoreCase = ค้นหาแบบไม่สนใจตัวพิมพ์เล็ก/ใหญ่
        List<News> topicResults = newsRepository.findByRemovedFalseAndTopicContainingIgnoreCase(keyword, Pageable.unpaged()).getContent();
        results.addAll(topicResults); // เพิ่มเข้า Set

        // ค้นหาใน shortDetail (รายละเอียดสั้น)
        List<News> shortDetailResults = newsRepository.findByRemovedFalseAndShortDetailContainingIgnoreCase(keyword, Pageable.unpaged()).getContent();
        results.addAll(shortDetailResults);

        // ค้นหาใน reporter (ผู้รายงาน)
        List<News> reporterResults = newsRepository.findByRemovedFalseAndReporterContainingIgnoreCase(keyword, Pageable.unpaged()).getContent();
        results.addAll(reporterResults);

        // แปลง Set → List → Page (เพื่อทำ pagination)
        return createPageFromList(new ArrayList<>(results), pageable);
    }

    /**
     * ดึงข่าวทั้งหมดที่มองเห็นได้ (ไม่รวมข่าวที่ถูกลบ)
     */
    @Override
    public Page<News> findAllVisible(Pageable pageable) {
        // findByRemovedFalse = หาข่าวที่ removed = false
        return newsRepository.findByRemovedFalse(pageable);
    }

    // ========================================
    // สำหรับ Admin
    // แสดงข่าวทั้งหมด รวมถึงข่าวที่ removed = true
    // ========================================

    /**
     * ค้นหาข่าวด้วย keyword สำหรับ admin
     * ต่างจาก searchByKeyword ตรงที่รวมข่าวที่ถูกลบด้วย
     */
    @Override
    public Page<News> searchByKeywordIncludingRemoved(String keyword, Pageable pageable) {
        Set<News> results = new LinkedHashSet<>();

        // ไม่มี "RemovedFalse" = ค้นหาทุกข่าว รวมที่ถูกลบ
        List<News> topicResults = newsRepository.findByTopicContainingIgnoreCase(keyword, Pageable.unpaged()).getContent();
        results.addAll(topicResults);

        List<News> shortDetailResults = newsRepository.findByShortDetailContainingIgnoreCase(keyword, Pageable.unpaged()).getContent();
        results.addAll(shortDetailResults);

        List<News> reporterResults = newsRepository.findByReporterContainingIgnoreCase(keyword, Pageable.unpaged()).getContent();
        results.addAll(reporterResults);

        return createPageFromList(new ArrayList<>(results), pageable);
    }

    /**
     * ดึงข่าวทั้งหมด รวมข่าวที่ถูกลบ (สำหรับ admin)
     */
    @Override
    public Page<News> findAll(Pageable pageable) {
        return newsRepository.findAll(pageable);
    }

    // ========================================
    // ค้นหา + กรองสถานะ (Normal Users)
    // ========================================

    /**
     * ค้นหา keyword + status
     */

    // SEARCH: ค้นหา keyword
    @Override
    public Page<News> searchByKeywordAndStatus(String keyword, String status, Pageable pageable) {
        Set<News> results = new LinkedHashSet<>();

        // Step 1: ค้นหา keyword (เหมือนเดิม)
        List<News> topicResults = newsRepository.findByRemovedFalseAndTopicContainingIgnoreCase(keyword, Pageable.unpaged()).getContent();
        results.addAll(topicResults);

        List<News> shortDetailResults = newsRepository.findByRemovedFalseAndShortDetailContainingIgnoreCase(keyword, Pageable.unpaged()).getContent();
        results.addAll(shortDetailResults);

        List<News> reporterResults = newsRepository.findByRemovedFalseAndReporterContainingIgnoreCase(keyword, Pageable.unpaged()).getContent();
        results.addAll(reporterResults);

        // Step 2: กรองตามสถานะ (real/fake/equal)
        List<News> filteredByStatus = filterByStatus(new ArrayList<>(results), status);

        // Step 3: แบ่งหน้า
        return createPageFromList(filteredByStatus, pageable);
    }

    /**
     * ดึงข่าวทั้งหมด แล้วกรองเฉพาะสถานะที่ต้องการ
     */
    @Override
    public Page<News> findAllVisibleByStatus(String status, Pageable pageable) {
        // ดึงข่าวที่มองเห็นทั้งหมด (ไม่รวมข่าวที่ถูกลบ)
        List<News> allVisible = newsRepository.findByRemovedFalse(Pageable.unpaged()).getContent();

        // กรองตามสถานะ
        List<News> filteredByStatus = filterByStatus(allVisible, status);

        // แบ่งหน้า
        return createPageFromList(filteredByStatus, pageable);
    }

    // ========================================
    // ค้นหา + กรองสถานะ (Admin)
    // ========================================

    /**
     * ค้นหา keyword + กรองสถานะ สำหรับ admin
     * ต่างจากของ normal user ตรงที่รวมข่าวที่ถูกลบด้วย
     */
    @Override
    public Page<News> searchByKeywordAndStatusIncludingRemoved(String keyword, String status, Pageable pageable) {
        Set<News> results = new LinkedHashSet<>();

        // ค้นหาทุกข่าว (รวมที่ถูกลบ)
        List<News> topicResults = newsRepository.findByTopicContainingIgnoreCase(keyword, Pageable.unpaged()).getContent();
        results.addAll(topicResults);

        List<News> shortDetailResults = newsRepository.findByShortDetailContainingIgnoreCase(keyword, Pageable.unpaged()).getContent();
        results.addAll(shortDetailResults);

        List<News> reporterResults = newsRepository.findByReporterContainingIgnoreCase(keyword, Pageable.unpaged()).getContent();
        results.addAll(reporterResults);

        // กรองตามสถานะ (แบบ admin - รวม removed)
        List<News> filteredByStatus = filterByStatusIncludingRemoved(new ArrayList<>(results), status);
        return createPageFromList(filteredByStatus, pageable);
    }

    /**
     * ดึงข่าวทั้งหมดและกรองตามสถานะ สำหรับ admin
     */
    @Override
    public Page<News> findAllByStatus(String status, Pageable pageable) {
        // ดึงทุกข่าว (รวมที่ถูกลบ)
        List<News> allNews = newsRepository.findAll(Pageable.unpaged()).getContent();

        // กรองตามสถานะ
        List<News> filteredByStatus = filterByStatusIncludingRemoved(allNews, status);

        return createPageFromList(filteredByStatus, pageable);
    }

    // ========================================
    // Helper Methods (ฟังก์ชันช่วย)
    // ========================================

    /**
     * กรองข่าวตามสถานะ (สำหรับ normal users)
     *
     * @param newsList - รายการข่าวที่จะกรอง
     * @param status - สถานะที่ต้องการ (real, fake, equal, all)
     * @return รายการข่าวที่ผ่านการกรอง
     */

    // FILTER: : กรองข่าวตามสถานะ
    private List<News> filterByStatus(List<News> newsList, String status) {
        // ถ้า status = null หรือ "all" = ไม่ต้องกรอง ส่งกลับทั้งหมด
        if (status == null || status.equalsIgnoreCase("all")) {
            return newsList;
        }

        // กรองข่าวด้วย Java Stream
        return newsList.stream()
                .filter(news -> { // filter = เก็บเฉพาะที่ตรงเงื่อนไข
                    String newsStatus = calculateStatus(news); // คำนวณว่าข่าวนี้เป็น real/fake/equal
                    return newsStatus.equalsIgnoreCase(status); // เช็คว่าตรงกับที่ต้องการหรือไม่
                })
                .collect(Collectors.toList()); // รวบรวมผลลัพธ์เป็น List
    }

    /**
     * กรองข่าวตามสถานะ (สำหรับ admin - รวม removed)
     *
     * ต่างจาก filterByStatus ตรงที่:
     * - กรอง "removed" ได้ (ข่าวที่ถูกลบ)
     * - ถ้ากรองสถานะอื่น (real/fake/equal) จะไม่เอาข่าวที่ถูกลบมาด้วย
     */
    private List<News> filterByStatusIncludingRemoved(List<News> newsList, String status) {
        if (status == null || status.equalsIgnoreCase("all")) {
            return newsList; // ส่งกลับทั้งหมด
        }

        // กรณีพิเศษ: ถ้าต้องการเฉพาะข่าวที่ถูกลบ
        if (status.equalsIgnoreCase("removed")) {
            return newsList.stream()
                    .filter(News::isRemoved) // เก็บเฉพาะข่าวที่ removed = true
                    .collect(Collectors.toList());
        }

        // กรณีปกติ: กรองตามสถานะ real/fake/equal
        return newsList.stream()
                .filter(news -> !news.isRemoved()) // ไม่เอาข่าวที่ถูกลบ
                .filter(news -> {
                    String newsStatus = calculateStatus(news);
                    return newsStatus.equalsIgnoreCase(status);
                })
                .collect(Collectors.toList());
    }

    /**
     * คำนวณสถานะของข่าวจากจำนวนโหวต
     *
     * Logic:
     * - ถ้า realVotes > fakeVotes = ข่าวจริง (real)
     * - ถ้า fakeVotes > realVotes = ข่าวปลอม (fake)
     * - ถ้าเท่ากัน = equal
     *
     * @return "real", "fake", หรือ "equal"
     */
    private String calculateStatus(News news) {
        // ดึงจำนวน votes (ถ้าเป็น null ให้เป็น 0)
        int realVotes = news.getRealVotes() != null ? news.getRealVotes() : 0;
        int fakeVotes = news.getFakeVotes() != null ? news.getFakeVotes() : 0;

        if (realVotes > fakeVotes) {
            return "real"; // ข่าวจริง
        } else if (realVotes < fakeVotes) {
            return "fake"; // ข่าวปลอม
        } else {
            return "equal"; // โหวตเท่ากัน
        }
    }

    /**
     * แปลง List → Page (สำหรับ Pagination)
     * @param list - รายการข่าวทั้งหมด
     * @param pageable - ข้อมูล pagination (หน้าที่เท่าไหร่, แสดงกี่รายการต่อหน้า)
     * @return Page<News> - ข่าวเฉพาะหน้าที่ต้องการ + ข้อมูล metadata
     */
    private Page<News> createPageFromList(List<News> list, Pageable pageable) {
        // คำนวณ index เริ่มต้น
        // getOffset() = หน้าที่ * จำนวนต่อหน้า
        int start = (int) pageable.getOffset();

        // คำนวณ index สุดท้าย
        int end = Math.min((start + pageable.getPageSize()), list.size());

        // ดึงเฉพาะรายการในช่วงที่ต้องการ
        // ถ้า start >= list.size() = หน้านี้ไม่มีข้อมูล ส่งกลับ list ว่าง
        List<News> pageContent = start >= list.size() ? List.of() : list.subList(start, end);

        // สร้าง Page object
        // PageImpl = implementation ของ Page interface
        // เก็บทั้ง:
        // - pageContent = รายการในหน้านี้
        // - pageable = ข้อมูล pagination
        // - list.size() = จำนวนรายการทั้งหมด (สำหรับคำนวณจำนวนหน้า)
        return new PageImpl<>(pageContent, pageable, list.size());
    }
}