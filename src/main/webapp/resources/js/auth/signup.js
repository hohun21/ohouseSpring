(function ($) {
    "use strict";

    $(function () {
        const contextPath = document.body.dataset.contextPath || "";
        const idRegex = /^[A-Za-z0-9_-]{4,20}$/;
        const passwordRegex = /^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z0-9!@#$%^&*()_+=?.-]{8,}$/;
        const $id = $("input[name='id']");
        const $name = $("input[name='name']");
        const $idMessage = $("#idCheck");
        const $nameMessage = $("#nameCheck");
        let idAvailable = false;
        let nameAvailable = false;
        let idRequest = null;
        let nameRequest = null;

        $id.on("input", function () {
            idAvailable = false;
            if (idRequest) idRequest.abort();
            const value = $.trim($id.val());
            if (!value) return $idMessage.css("color", "red").text("아이디를 입력해주세요.");
            if (!idRegex.test(value)) return $idMessage.css("color", "red").text("아이디는 영문, 숫자, 하이픈, 밑줄 4~20자만 가능합니다.");
            $idMessage.css("color", "").text("아이디 중복 확인이 필요합니다.");
        });

        $("#btnIDDuplicateCheck").on("click", function () {
            const value = $.trim($id.val());
            if (!idRegex.test(value)) {
                $idMessage.css("color", "red").text("아이디를 영문, 숫자, 하이픈, 밑줄 4~20자로 입력해주세요.");
                $id.trigger("focus");
                return;
            }
            $idMessage.css("color", "").text("아이디 중복 확인 중입니다.");
            $(this).prop("disabled", true);
            idRequest = $.ajax({
                url: contextPath + "/auth/idcheck.ajax",
                type: "GET",
                dataType: "json",
                data: { id: value },
                cache: false
            }).done(function (result) {
                if ($.trim($id.val()) !== value) return;
                idAvailable = Number(result.count) === 0;
                $idMessage.css("color", idAvailable ? "green" : "red")
                    .text(idAvailable ? "사용 가능한 아이디입니다." : "이미 사용 중인 아이디입니다.");
            }).fail(function (xhr, status) {
                if (status !== "abort") $idMessage.css("color", "red").text("중복 확인 중 오류가 발생했습니다.");
            }).always(function () {
                idRequest = null;
                $("#btnIDDuplicateCheck").prop("disabled", false);
            });
        });

        $name.on("input", function () {
            nameAvailable = false;
            if (nameRequest) nameRequest.abort();
            $nameMessage.css("color", "").text("다른 유저와 겹치지 않도록 입력해주세요. (2~20자)");
        });

        $name.on("blur", function () {
            const value = $.trim($name.val());
            if (value.length < 2 || value.length > 20) {
                nameAvailable = false;
                $nameMessage.css("color", "red").text("이름은 2~20자로 입력해주세요.");
                return;
            }
            $nameMessage.css("color", "").text("이름 중복 확인 중입니다.");
            nameRequest = $.ajax({
                url: contextPath + "/auth/namecheck.ajax",
                type: "GET",
                dataType: "json",
                data: { name: value },
                cache: false
            }).done(function (result) {
                if ($.trim($name.val()) !== value) return;
                nameAvailable = Number(result.count) === 0;
                $nameMessage.css("color", nameAvailable ? "green" : "red")
                    .text(nameAvailable ? "사용 가능한 이름입니다." : "사용 중인 이름입니다.");
            }).fail(function (xhr, status) {
                if (status !== "abort") $nameMessage.css("color", "red").text("이름 확인 중 오류가 발생했습니다.");
            }).always(function () {
                nameRequest = null;
            });
        });

        function validatePasswords() {
            const password = $("input[name='password']").val();
            const confirmation = $("input[name='passwordConfirm']").val();
            const valid = passwordRegex.test(password) && password === confirmation;
            $("#pwdRegex").css("color", passwordRegex.test(password) ? "green" : "red");
            $("#pwdCheck").css("color", password === confirmation && confirmation ? "green" : "red")
                .text(password === confirmation && confirmation ? "비밀번호가 일치합니다." : "비밀번호가 일치하지 않습니다.");
            return valid;
        }
        $("input[name='password'], input[name='passwordConfirm']").on("input", validatePasswords);

        $("#signupForm").on("submit", function (event) {
            const id = $.trim($id.val());
            const name = $.trim($name.val());
            if (!idAvailable || !idRegex.test(id)) {
                event.preventDefault();
                $idMessage.css("color", "red").text("사용 가능한 아이디인지 중복 확인해주세요.");
                return;
            }
            if (!nameAvailable || name.length < 2 || name.length > 20) {
                event.preventDefault();
                $nameMessage.css("color", "red").text("사용 가능한 이름인지 확인해주세요.");
                return;
            }
            if (!validatePasswords()) {
                event.preventDefault();
                window.alert("비밀번호 형식과 비밀번호 확인을 다시 확인해주세요.");
            }
        });

        $("#checkAll").on("change", function () {
            $("input[name='agreeAge'], input[name='agreeTerms'], input[name='agreeMarketing'], input[name='agreeEvent']")
                .prop("checked", this.checked);
        });
    });
})(jQuery);
