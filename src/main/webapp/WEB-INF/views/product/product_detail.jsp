<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/WEB-INF/views/layout/header.jsp"/>
<link rel="stylesheet" href="/resources/css/product_detail.css"/>

<div class="product-detail">

    <!-- 카테고리 경로: DB 데이터만 사용 -->
    <div class="breadcrumb">
        <c:forEach var="category" items="${pdto.categoryDTOList}" varStatus="s">
            <button class="link-category" data-category_id="${category.category_id}">
                <span>${category.category_name}</span>
            </button>
            <c:if test="${!s.last}">
                <span>›</span>
            </c:if>
        </c:forEach>
    </div>

    <div class="product-main">

        <!-- 왼쪽 이미지 -->
        <div class="gallery">

            <div class="thumbs">
                <c:forEach var="image" items="${pdto.imageDTOList}" varStatus="s">
                    <div class="thumb ${s.first ? 'active' : ''}"
                         onmouseover="changeImage(this)">
                        <img src="${image.image_url}"
                             alt="${pdto.productDTO.product_name}">
                    </div>
                </c:forEach>
            </div>

            <div class="main-image">
                <c:if test="${not empty pdto.imageDTOList}">
                    <img id="mainProductImage"
                         src="${pdto.imageDTOList[0].image_url}"
                         alt="${pdto.productDTO.product_name}">
                </c:if>
            </div>

        </div>

        <!-- 오른쪽 상품 정보 -->
        <div class="product-info">

            <div class="brand" data-brand-id="${pdto.productDTO.brand_id}"
                 data-brand-name="${pdto.productDTO.brand_name}">
                ${pdto.productDTO.brand_name}
            </div>
            <div class="product-name">
                <h1 class="title">
                    ${pdto.productDTO.product_name}
                </h1>
            </div>

            <div class="price-box">
                <span class="discount">
                        <fmt:formatNumber value="${pdto.productDTO.discount_rate}" type="number" maxFractionDigits="0"/>%
                </span>

                <span class="original-price">
                   <fmt:formatNumber value="${pdto.productDTO.original_price}" pattern="#,###"/>원
                </span>

                <div>
                    <span class="price">
                        <fmt:formatNumber value="${pdto.productDTO.price}" pattern="#,###"/>원
                    </span>

                </div>
            </div>

            <div class="info">

                <div class="info-row">
                    <div class="info-label">카테고리</div>
                    <div class="category-list">
                        <c:forEach var="category"
                                   items="${pdto.categoryDTOList}">
                            <span>${category.category_name}</span>
                        </c:forEach>
                    </div>
                </div>

                <div class="info-row">
                    <div class="info-label">상품번호</div>
                    <div>${param.product_id}</div>
                </div>

            </div>

            <!-- 옵션 -->
            <div class="option-area">

                <c:set var="lastGroupId" value="-1"/>
                <c:set var="requiredGroupCount" value="0"/>

                <c:forEach var="option"
                           items="${pdto.optionDTOList}">

                    <c:if test="${lastGroupId != option.option_group_id}">

                        <c:set var="lastGroupId"
                               value="${option.option_group_id}"/>

                        <c:if test="${option.required == '1'}">

                            <c:set var="requiredGroupCount"
                                   value="${requiredGroupCount + 1}"/>

                        </c:if>

                        <select class="option-select"
                                data-group_id="${option.option_group_id}"
                                data-group_name="${option.group_name}"
                                data-required="${option.required}"

                                <c:if test="${option.required == '1' && requiredGroupCount > 1}">
                                    disabled
                                </c:if>
                        >

                            <option value=""
                                    selected
                                    disabled>
                                    ${option.group_name}을 선택해주세요
                            </option>

                            <c:forEach var="value"
                                       items="${pdto.optionDTOList}">


                                <c:if test="${value.option_group_id == option.option_group_id}">

                                    <option value="${value.option_value_id}">
                                            ${value.option_name}</option>

                                </c:if>

                            </c:forEach>

                        </select>

                    </c:if>

                </c:forEach>
                <div id="optionToast"></div>
                <span id="optionWarning">
                         옵션을 선택해주세요
                    </span>

                <div id="selectedList"></div>

                <div class="total">
                    <span>주문금액</span>
                    <span id="totalPrice">0원</span>
                </div>


                <div class="buttons">

                    <button type="button"
                            class="cart">
                        장바구니
                    </button>

                    <button type="button"
                            class="buy">
                        바로구매
                    </button>

                </div>
            </div>
        </div>
        <div class="product-info-detail">
            <div class="tabs">
                <a class="active" href="#detail">상품정보</a>
                <a href="#detail-review">리뷰</a>
                <a href="#qna">문의</a>
                <a href="#delivery">배송/환불</a>
            </div>
            <!-- 1. 상품정보 (기본으로 보임: active) -->
            <section id="detail" class="tab-content active">
                <h2>상품정보</h2>
                <div class="detail-empty">
                    <c:forEach var="image" items="${pdto.imageDTOList}" varStatus="s">
                        <c:if test="${s.first}">
                            <img src="${image.image_url}"
                                 alt="${pdto.productDTO.product_name}">
                        </c:if>
                    </c:forEach>
                </div>
            </section>

            <!-- 2. 리뷰 -->
           	<section id="detail-review" class="tab-content">
                <jsp:include page="/WEB-INF/views/product/review/reviewList.jsp">
                    <jsp:param name="product_id" value="${pdto.productDTO.product_id}"/>
                    <jsp:param name="member_id" value="${memberId != null ? memberId : 2}"/>
                </jsp:include>
            </section> 
            <script type="text/javascript">
         // 기존 탭 클릭 이벤트(혹은 상품 상세 페이지 스크립트 내부에 추가)
            $('.tabs a').on('click', function(e) {
                e.preventDefault();
                const target = $(this).attr('href'); // 예: #detail-review
                
                $('.tabs a').removeClass('active');
                $(this).addClass('active');
                
                $('.tab-content').removeClass('active');
                $(target).addClass('active');

                // 만약 누른 탭이 리뷰 탭(#detail-review)이고, 아직 리뷰 목록이 로드되지 않았거나 비어있다면 최초 로드 수행
                if (target === '#detail-review') {
                    var reviewListContainer = document.getElementById('reviewListContainer');
                    // 내용이 비어있거나 초기 상태라면 첫 페이지 데이터 호출
                    if (reviewListContainer && (!reviewListContainer.innerHTML.trim() || reviewListContainer.querySelectorAll('.review-item').length === 0)) {
                        // reviewList.js 내부의 펑션이나 아래와 같이 직접 호출 가능
                        if (typeof triggerReviewFetch === 'function') {
                            triggerReviewFetch('1', 'best');
                        }
                    }
                }
            });
            </script>
           
			<!-- 3. 문의 -->
            <section id="qna" class="tab-content">
                <h2>문의</h2>
            </section>

            <!-- 4. 배송/환불 -->
            <section id="delivery" class="tab-content">
                <h2>배송/환불</h2>
            </section>
        </div>


    </div>
</div>
<script src="${pageContext.request.contextPath}/resources/js/productDetail.js"></script>
<jsp:include page="/WEB-INF/views/product/review/reviewFormModal.jsp">

    <jsp:param name="productId" value="${pdto.productDTO.product_id}"/>
</jsp:include>
<jsp:include page="/WEB-INF/views/product/review/reviewEditModal.jsp">
    <jsp:param name="productId" value="${pdto.productDTO.product_id}"/>
</jsp:include>

<script>
    $(".link-category").on("click", function (e) {
        const category_id = $(this).data("category_id");
        location.href = `${pageContext.request.contextPath}/shopping/category/category.htm?category_id=\${category_id}`;
    })
    const contextPath = "${pageContext.request.contextPath}";
</script>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>