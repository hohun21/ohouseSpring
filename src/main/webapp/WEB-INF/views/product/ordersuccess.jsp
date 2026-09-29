<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp"/>

<div class="payment-success">
    <div class="success-container">

        <div class="success-icon">✓</div>

        <h1>주문 번호 : ${tossOrderId}</h1>
        <h1>${orderName} 건</h1>
        <h1>결제가 완료되었습니다.</h1>

        <p>주문이 정상적으로 접수되었습니다.</p>
        <p>이용해 주셔서 감사합니다.</p>

        <div class="success-actions">

            <a href="${pageContext.request.contextPath}/main.htm"
               class="btn-home">
                홈으로 가기
            </a>

            <a href="${pageContext.request.contextPath}/member/myshopping.htm"
               class="btn-order">
                주문내역 확인
            </a>

        </div>

    </div>
</div>

<style>
    .payment-success {
        min-height: calc(100vh - 160px);
        display: flex;
        justify-content: center;
        align-items: center;
        padding: 60px 20px;
        box-sizing: border-box;
    }

    .success-container {
        width: 100%;
        max-width: 560px;
        box-sizing: border-box;

        padding: 55px 40px;

        text-align: center;

        background: #fff;
        border: 1px solid #eee;
        border-radius: 14px;

        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
    }

    .success-icon {
        width: 76px;
        height: 76px;

        margin: 0 auto 30px;

        display: flex;
        align-items: center;
        justify-content: center;

        border-radius: 50%;

        background: #35c5f0;
        color: #fff;

        font-size: 44px;
        font-weight: 700;
    }

    .success-container h1 {
        margin: 0 0 12px;

        font-size: 24px;
        font-weight: 600;
        line-height: 1.4;

        word-break: keep-all;
    }

    .success-container h1:last-of-type {
        margin-top: 20px;
        margin-bottom: 25px;

        font-size: 28px;
        font-weight: 700;
    }

    .success-container p {
        margin: 6px 0;

        color: #777;
        font-size: 15px;
        line-height: 1.5;
    }

    .success-actions {
        display: flex;
        justify-content: center;
        gap: 10px;

        margin-top: 35px;
    }

    .success-actions a {
        display: inline-flex;
        align-items: center;
        justify-content: center;

        min-width: 130px;
        padding: 13px 20px;

        border-radius: 7px;

        text-decoration: none;
        font-size: 14px;
        font-weight: 600;

        box-sizing: border-box;
    }

    .btn-home {
        border: 1px solid #ddd;
        background: #fff;
        color: #555;
    }

    .btn-home:hover {
        background: #f7f7f7;
    }

    .btn-order {
        background: #35c5f0;
        color: #fff;
        border: 1px solid #35c5f0;
    }

    .btn-order:hover {
        background: #22b6e3;
    }

    @media (max-width: 600px) {

        .payment-success {
            min-height: calc(100vh - 120px);
            padding: 30px 15px;
        }

        .success-container {
            padding: 40px 20px;
        }

        .success-container h1 {
            font-size: 20px;
        }

        .success-container h1:last-of-type {
            font-size: 24px;
        }

        .success-actions {
            flex-direction: column;
        }

        .success-actions a {
            width: 100%;
        }
    }
</style>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>