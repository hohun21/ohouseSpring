(function ($) {
    "use strict";

    $(function () {
        const contextPath = document.body.dataset.contextPath || "";
        const params = new URLSearchParams(window.location.search);
        if (params.get("error") === "true") {
            window.alert("아이디 또는 비밀번호가 잘못되었습니다.\n다시 확인해주세요.");
        }

        $("#loginForm").on("submit", function (event) {
		    const $id = $("#id");
		    const $password = $("#password");
		
		    const idEmpty = !$.trim($id.val());
		    const passwordEmpty = !$.trim($password.val());
		
		    $id.toggleClass("input-error", idEmpty);
		    $password.toggleClass("input-error", passwordEmpty);
		
		    if (idEmpty || passwordEmpty) {
		        event.preventDefault();
		
		        if (idEmpty) {
		            $id.trigger("focus");
		        } else {
		            $password.trigger("focus");
		        }
		    }
		});

		// 입력을 시작하면 해당 칸의 빨간 테두리 제거
		$("#id, #password").on("input", function () {
		    if ($.trim($(this).val())) {
		        $(this).removeClass("input-error");
		    }
		});
    });
})(jQuery);
