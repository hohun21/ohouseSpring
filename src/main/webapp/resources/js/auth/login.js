(function ($) {
    "use strict";

    $(function () {
        const contextPath = document.body.dataset.contextPath || "";
        const params = new URLSearchParams(window.location.search);
        if (params.get("error") === "true") {
            window.alert("아이디 또는 비밀번호가 잘못되었습니다.\n다시 확인해주세요.");
        }

        $("#loginForm").on("submit", function (event) {
            event.preventDefault();
            const form = this;
            const id = $.trim($("#id").val());
            if (!id) {
                window.alert("아이디를 입력해주세요.");
                $("#id").trigger("focus");
                return;
            }

            $.ajax({
                url: contextPath + "/auth/statusCheck.ajax",
                type: "GET",
                dataType: "json",
                data: { id: id }
            }).done(function (result) {
                if (result.code === "WITHDRAWN") {
                    window.alert("탈퇴한 회원입니다.");
                } else if (result.code === "STOP") {
                    window.alert("정지된 회원입니다.");
                } else if (result.code === "NOT_FOUND") {
                    window.alert("등록되지 않은 아이디입니다.");
                } else if (result.code === "ACTIVE") {
                    form.submit();
                } else {
                    window.alert("회원 상태를 확인할 수 없습니다.");
                }
            }).fail(function (xhr) {
                console.error("회원 상태 확인 실패:", xhr.status, xhr.responseText);
                window.alert("서버 통신 중 오류가 발생했습니다.");
            });
        });
    });
})(jQuery);
