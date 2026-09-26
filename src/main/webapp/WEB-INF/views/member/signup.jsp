<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>회원가입 - 오늘의집</title>
<!-- 프리텐다드 폰트 적용 -->
<link rel="stylesheet" as="style" crossorigin
	href="https://cdn.jsdelivr.net/gh/orioncactus/pretendard@v1.3.9/dist/web/static/pretendard.min.css" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/auth/signup.css">
</head>
<body data-context-path="${pageContext.request.contextPath}">

	<header class="header-logo">
		<a href="main.htm" class="logo-area">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 481 136">
                <path fill="#111" d="M459.317 41.715H443.04c.134 2.783 1.008 5.303 4.532 8.903s9.133 7.32 15.825 11.457l-6.497 10.51c-6.865-4.243-13.264-8.501-17.985-13.322-.983-1.004-1.92-1.884-2.791-2.98a30 30 0 0 1-2.791 3.323c-4.721 4.82-11.12 9.251-17.985 13.494l-6.497-10.51c6.692-4.137 12.3-8.03 15.825-11.63 3.525-3.599 4.398-6.462 4.532-9.245h-16.277V30.73h46.386zM202.316 29.28c17.481 0 26.613 11.252 26.613 24.263v1.758c0 11.355-6.9 21.37-20.325 23.736V91.98h32.522v10.983h-78.087V91.981h32.522V79.04c-13.441-2.358-20.349-12.378-20.349-23.74v-1.758c0-13.01 9.132-24.263 26.613-24.263zm-.245 10.64c-9.429 0-14.073 6.716-14.073 13.79v1.424c0 7.074 4.714 13.79 14.073 13.79s14.073-6.716 14.073-13.79V53.71c0-7.074-4.645-13.79-14.073-13.79m197.014 73.188h-13.043V94.844c-3.364.484-9.354 1.26-18.175 2.086-14.622 1.372-40.328 1.125-40.328 1.125l-.209-11.819s25.813.169 39.599-.952c9.613-.782 16.079-1.739 19.113-2.247V26.154h13.043zM353.77 30.996c14.752 0 22.408 9.841 22.408 21.089v1.757c0 11.247-7.728 21.088-22.408 21.088l-.491-.005c-14.68 0-22.408-9.836-22.408-21.083v-1.757c0-11.248 7.656-21.089 22.408-21.089zm-.245 10.726c-6.727 0-10.126 5.376-10.126 10.706v1.07c0 5.897 3.399 10.684 10.127 10.706 6.662.022 10.124-4.82 10.124-10.706v-1.07c0-5.33-3.462-10.706-10.125-10.706M268.667 43.4h44.696v10.813h-57.739V26.85h13.043zm-23.778 26.215h78.086V58.803h-78.086z" />
                <path fill="#111" d="M479.999 99.939c-.011 10.413-2.221 12.533-12.761 12.54-11.69.007-18.439.011-30.148 0-10.245-.01-12.616-2.046-12.697-11.824-.077-9.232.002-25.554.002-25.567h13.043v9.153h29.69V26.155H480s.03 45.359-.001 73.784m-42.561 1.728h29.69V94.71h-29.69zM313.005 74.209l.004 24.48h-45.043v4.739h46.979v10.297h-47.151c-10.653 0-12.846-2.098-12.871-12.595-.01-4.223 0-12.396 0-12.396h45.039v-4.4h-44.691V74.208z" />
                <path fill="#1496f4" d="M75.001 1.243a20.72 20.72 0 0 0-14.136 0c-8.591 3.093-36.22 21.208-51.007 36.46C1.88 45.928 0 51.618 0 61.48v5.078c.126 15.644.914 34.269 3.675 43.324 4.773 15.652 11.949 25.984 57.295 25.984h13.926c45.345 0 52.521-10.332 57.295-25.984 2.761-9.055 3.549-27.68 3.675-43.325v-5.078c0-9.861-1.882-15.551-9.858-23.777-14.789-15.25-42.414-33.366-51.007-36.459" />
            </svg>
		</a>
	</header>

	<main class="signup-container">
		<h1 class="page-title">회원가입</h1>
		<c:if test="${not empty signupError}"><p class="sub-text form-error"><c:out value="${signupError}" /></p></c:if>

		<div class="sns-area">
			<div class="sns-title">SNS계정으로 간편하게 회원가입</div>
			<div class="sns-buttons">
				<button class="sns-btn facebook" type="button">f</button>
				<button class="sns-btn kakao" type="button">TALK</button>
				<button class="sns-btn naver" type="button">N</button>
			</div>
		</div>

		<div class="divider"></div>

		<form id="signupForm" action="${pageContext.request.contextPath}/auth/signup.htm" method="post">
			<!-- 아이디 -->
			<div class="form-group">
				<label class="label">아이디</label>
				<div class="id-wrap">
					<input type="text" name="id" placeholder="아이디 (영문, 숫자 4~20자)" required>
					<button type="button" id="btnIDDuplicateCheck" class="btn-verify">중복확인</button>
				</div>
				<p id="idCheck" class="sub-text"></p>
				<c:if test="${errors.id}">
					<p class="sub-text form-error">아이디를 입력해주세요.</p>
                </c:if>
			</div>
			


			<!-- 비밀번호 -->
			<div class="form-group">
				<label class="label">비밀번호</label> 
                <span class="sub-text" id="pwdRegex">
                    영문, 숫자를 포함한 8자 이상의 비밀번호를 입력해주세요.
                </span> 
				<input type="password" name="password" class="input-box" placeholder="비밀번호" required>
			</div>

			<!-- 비밀번호 확인 -->
			<div class="form-group">
				<label class="label">비밀번호 확인</label> 
                <span id="pwdCheck" class="sub-text"></span> 
                <input type="password" name="passwordConfirm" class="input-box" placeholder="비밀번호 확인" required>
			</div>
			


			<!-- 이름(별명) -->
			<div class="form-group">
				<label class="label">이름(별명)</label> 
                <span id="nameCheck" class="sub-text">다른 유저와 겹치지 않도록 입력해주세요. (2~20자)</span> 
                <input type="text" name="name" class="input-box" placeholder="이름 (2~20자)" required>
			</div>
			

			<!-- 약관 동의 -->
			<div class="form-group">
				<label class="label">약관동의</label>
				<div class="terms-box">
					<label class="term-item bold">
						<div class="term-left">
							<input type="checkbox" id="checkAll"> <span>전체동의 <span class="term-sub">선택항목에 대한 동의 포함</span></span>
						</div>
					</label> 
                    <label class="term-item">
						<div class="term-left">
							<input type="checkbox" name="agreeAge" required> <span>만 14세 이상입니다 <span class="term-sub term-required">(필수)</span></span>
						</div>
					</label> 
                    <label class="term-item">
						<div class="term-left">
							<input type="checkbox" name="agreeTerms" required> <span>이용약관 <span class="term-sub term-required">(필수)</span></span>
						</div> <span class="term-arrow">&gt;</span>
					</label> 
                    <label class="term-item">
						<div class="term-left">
							<input type="checkbox" name="agreeMarketing"> <span>개인정보 마케팅 활용 동의 <span class="term-sub">(선택)</span></span>
						</div> <span class="term-arrow">&gt;</span>
					</label> 
                    <label class="term-item">
						<div class="term-left">
							<input type="checkbox" name="agreeEvent"> <span>이벤트, 쿠폰, 특가 알림 메일 및 SMS 등 수신 <span class="term-sub">(선택)</span></span>
						</div>
					</label>
				</div>
			</div>

			<!-- 리캡챠 -->
			<div class="recaptcha-box">
				<div class="recaptcha-left">
					<div class="recaptcha-checkbox"></div>
					<span>로봇이 아닙니다.</span>
				</div>
				<div class="recaptcha-right">
					<div class="recaptcha-icon">♻️</div>
					<div>reCAPTCHA</div>
				</div>
			</div>

			<button type="submit" class="btn-submit">회원가입하기</button>
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
		</form>

		<div class="login-link">
			이미 아이디가 있으신가요? <a href="${pageContext.request.contextPath}/auth/login.htm">로그인</a>
		</div>
		<div class="seller-signup-link">
			오늘의집 파트너 가입 <a href="seller/signup.htm">회원가입</a>
		</div>
	</main>

<script src="${pageContext.request.contextPath}/resources/js/auth/signup.js"></script>
</body>
</html>