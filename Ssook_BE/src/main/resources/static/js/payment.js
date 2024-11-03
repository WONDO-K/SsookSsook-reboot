const buyButton = document.getElementById('payment');
var IMP = window.IMP;
IMP.init("imp84868367");

buyButton.onclick = function () {
    kakaoPay('{{loginId}}', '{{userId}}');
};

function kakaoPay(loginId, userId) {
    if (confirm("포인트 충전을 진행하시겠습니까?")) {
        const merchant_uid = "IMP" + new Date().getTime();
        const amount = 100; // 충전 금액

        IMP.request_pay({
            pg: 'kakaopay.TC0ONETIME',
            pay_method: 'card',
            merchant_uid: merchant_uid,
            name: '포인트 충전',
            amount: amount,
            buyer_email: loginId,  // loginId를 buyer_email에 할당
            buyer_name: userId     // userId를 buyer_name에 할당
        }, function (rsp) { // 결제 완료 후 callback
            if (rsp.success) {
                // 충전 완료 후 서버에 포인트 충전 요청
                $.ajax({
                    url: "/api/payment/charge",
                    method: "POST",
                    data: JSON.stringify({ amount: rsp.paid_amount, userId: userId }), // userId로 식별
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
