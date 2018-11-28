$(document).ready(function() {
    console.log("Document is ready!");

    /* HOTP variables */
    var hotpGenSecretKeyBtn = $('#hotpGenSecretKeyBtn');
    var hotpSubmitBtn = $('#hotpSubmitBtn');

    var hotpPasswordLengthInput = $('#hotpPasswordLength');
    var hotpAlgorithmInput = $('#hotpAlgorithm');
    var hotpSecretKeyInput = $('#hotpSecretKey');
    var hotpCounterInput = $('#hotpCounter');

    /* TOTP variables */
    var totpGenSecretKeyBtn = $('#totpGenSecretKeyBtn');
    var totpSubmitBtn = $('#totpSubmitBtn');

    var totpPasswordLengthInput = $('#totpPasswordLength');
    var totpAlgorithmInput = $('#totpAlgorithm');
    var totpSecretKeyInput = $('#totpSecretKey');
    var totpCounterInput = $('#totpCounter');
    var totpTimeStep = $('#totpTimeStep');
    var totpNowField = $('#totpNow');

    var resultBox = $('#resultBox');
    var resultBoxCloseBtns = $('.close-modal');

    genSecretKey(hotpSecretKeyInput, 20);
    genSecretKey(totpSecretKeyInput, 20);
    totpCounterInput.val(Date.now());

    resultBoxCloseBtns.on('click', function () {
       resultBox.removeClass('is-active');
    });

    hotpGenSecretKeyBtn.on('click', function () {
        genSecretKey(hotpSecretKeyInput, 20);
    });
    totpGenSecretKeyBtn.on('click', function () {
        genSecretKey(totpSecretKeyInput, 20);
    });

    window.setInterval(function () {
        totpNowField.text(Date.now());
    }, 1000);

    hotpSubmitBtn.on('click', function (e) {
        e.preventDefault();

        if (!allHotpFieldsValid()) {
            alert("Bitte alle Felder ausfüllen!");
            return;
        }

        hotpSubmitBtn.toggleClass('is-loading');
        hotpSubmitBtn.attr('disabled', 'disabled');

        var jqxhr = $.ajax( "/hotp/" + buildHotpUrlParams(hotpPasswordLengthInput.val(), hotpAlgorithmInput.val(), hotpSecretKeyInput.val(), hotpCounterInput.val()))
            .done(function (data) {
                console.log("Success!");
                $.each(data, function (name, item) {
                    $('#' + name).text(item);
                });
                resultBox.addClass('is-active');
            })
            .fail(function (xhr, status, error) {
                alert("Error: " + error);
            })
            .always(function () {
                hotpSubmitBtn.toggleClass('is-loading');
                hotpSubmitBtn.attr('disabled', null);
            });
    });

    totpSubmitBtn.on('click', function (e) {
        e.preventDefault();

        if (!allTotpFieldsValid()) {
            alert("Bitte alle Felder ausfüllen!");
            return;
        }

        totpSubmitBtn.toggleClass('is-loading');
        totpSubmitBtn.attr('disabled', 'disabled');

        var jqxhr = $.ajax( "/totp/" + buildTotpUrlParams(totpPasswordLengthInput.val(), totpAlgorithmInput.val(), totpSecretKeyInput.val(), totpCounterInput.val(), totpTimeStep.val()))
            .done(function (data) {
                console.log("Success!");
                $.each(data, function (name, item) {
                    $('#' + name).text(item);
                });
                resultBox.addClass('is-active');
            })
            .fail(function (xhr, status, error) {
                alert("Error: " + error);
            })
            .always(function () {
                totpSubmitBtn.toggleClass('is-loading');
                totpSubmitBtn.attr('disabled', null);
            });
    });

    function allHotpFieldsValid() {
        return hotpPasswordLengthInput.val() && hotpAlgorithmInput.val() && hotpSecretKeyInput.val() && hotpCounterInput.val();
    }
    function allTotpFieldsValid() {
        return totpPasswordLengthInput.val() && totpAlgorithmInput.val() && totpSecretKeyInput.val() && totpCounterInput.val() && totpTimeStep.val();
    }

    function buildHotpUrlParams(passwordLength, algorithm, secretKey, counter) {
        return passwordLength + "/" + algorithm + "/" + secretKey + "/" + counter;
    }
    function buildTotpUrlParams(passwordLength, algorithm, secretKey, counter, timeStep) {
        return passwordLength + "/" + algorithm + "/" + secretKey + "/" + counter + "/" + timeStep;
    }

    function genSecretKey(secretKeyInput, length) {
        secretKeyInput.val(makeid(length));
    }

    function makeid(length) {
        var text = "";
        var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        for (var i = 0; i < length; i++)
            text += possible.charAt(Math.floor(Math.random() * possible.length));

        return text;
    }
});