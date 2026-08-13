package com.jikku.backend.domain.travelPost.entity;

import com.jikku.backend.domain.travelPost.enums.BlockType;
import com.jikku.backend.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

@Entity
@Table(name="travel_post_block")
// 블록 종류에 따라 채워야 할 칼럼이 갈린다. 어느 쪽도 아닌 반쪽 데이터는 DB에서 막는다 (FillMap과 같은 방식)
@Check(constraints = "(" +
  "block_type = 'TEXT' AND \"text\" IS NOT NULL AND img_url IS NULL" +
  ") OR (" +
  "block_type = 'IMAGE' AND img_url IS NOT NULL AND \"text\" IS NULL" +
  ")")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TravelPostBlock extends BaseTimeEntity {

    @Id
    @Column(name = "travel_post_block_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long travelPostBlockId;

    @Enumerated(value = EnumType.STRING)
    @Column(name="block_type",nullable = false)
    private BlockType blockType;

    @Column(name="sort_order",nullable = false)
    private Integer sortOrder;

    // 기록 본문은 varchar(255)를 넘길 수 있어 TEXT로 못 박는다
    @Column(name="text",columnDefinition = "TEXT")
    private String text;

    @Column(name="img_url")
    private String imgUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="travel_post_id",nullable = false)
    private TravelPost travelPost;

  public static TravelPostBlock of(
    TravelPost travelPost,
    BlockType blockType,
    Integer sortOrder,
    String text,
    String imgUrl
  ) {
    TravelPostBlock block = new TravelPostBlock();
    block.travelPost = travelPost;
    block.blockType = blockType;
    block.sortOrder = sortOrder;
    block.text = text;
    block.imgUrl = imgUrl;
    return block;
  }

}
