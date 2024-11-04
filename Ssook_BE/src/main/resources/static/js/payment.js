const buyButton = document.getElementById('payment');
var IMP = window.IMP;
IMP.init("imp84868367");

buyButton.onclick = function () {
    const loginId = document.getElementById('hiddenLoginId').value;  // 숨겨진 필드에서 값 가져오기
    const userId = parseInt(document.getElementById('hiddenUserId').value, 10); // 정수로 변환
    const amountInput = document.getElementById('amount').value;

    if (!amountInput || isNaN(amountInput) || amountInput <= 0) {
        alert("충전할 금액을 올바르게 입력하세요.");
        return;
    }0

    kakaoPay(loginId, userId, amountInput);
};

function kakaoPay(loginId, userId, amount) {
    if (confirm("포인트 충전을 진행하시겠습니까?")) {
        const merchant_uid = "IMP" + new Date().getTime();

        IMP.request_pay({
            pg: 'kakaopay.TC0ONETIME',
            pay_method: 'card',
            merchant_uid: merchant_uid,
            name: '포인트 충전',
            amount: amount, // 입력된 금액 사용
            buyer_email: loginId,  // loginId를 buyer_email에 할당
            buyer_name: userId     // userId를 buyer_name에 할당
        }, function (rsp) { // 결제 완료 후 callback
            if (rsp.success) {
                // 충전 완료 후 서버에 포인트 충전 요청
                $.ajax({
                    url: "/api/v1/pay/point/charge",
                    method: "POST",
                    data: JSON.stringify({
                        impUid: rsp.imp_uid, // impUid 포함
                        userId: userId,
                        amount: rsp.paid_amount
                    }),
                    contentType: "application/json",
                    success: function (response) {
                        if (response.status === 200) {
                            alert('포인트 충전 완료!');
                            window.location.reload();
                        } else {
                            alert(`error:[${response.status}]\n관리자에게 문의바랍니다.`);
                        }
                    }
                });
            } else {
                alert(rsp.error_msg);
            }
        });
    }
}
