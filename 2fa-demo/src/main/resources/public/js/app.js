$(document).ready(function() {
    console.log("Document is ready!");

    var genSecretKeyBtn = $('#genSecretKeyBtn');
    var hotpSubmitBtn = $('#hotpSubmitBtn');

    var passwordLengthInput = $('#passwordLength');
    var algorithmInput = $('#algorithm');
    var secretKeyInput = $('#secretKey');
    var counterInput = $('#counter');

    var resultBox = $('#resultBox');

    genSecretKeyBtn.on('ready click', function () {
        genSecretKey(secretKeyInput, 20);
    });

    hotpSubmitBtn.on('click', function (e) {
        e.preventDefault();

        if (!allFieldsValid()) {
            alert("Bitte alle Felder ausfüllen!");
            return;
        }

        hotpSubmitBtn.toggleClass('is-loading');
        hotpSubmitBtn.attr('disabled', 'disabled');

        var jqxhr = $.ajax( "/hotp/" + buildUrlParams(passwordLengthInput.val(), algorithmInput.val(), secretKeyInput.val(), counterInput.val()))
            .done(function (data) {
                console.log("Success!");
                $.each(data, function (name, item) {
                    $('#' + name).text(item);
                });
                resultBox.removeClass('is-invisible');
            })
            .fail(function (xhr, status, error) {
                alert("Error: " + error);
            })
            .always(function () {
                hotpSubmitBtn.toggleClass('is-loading');
                hotpSubmitBtn.attr('disabled', null);
            });
    });

    function allFieldsValid() {
        return passwordLengthInput.val() && algorithmInput.val() && secretKeyInput.val() && counterInput.val();
    }

    function buildUrlParams(passwordLength, algorithm, secretKey, counter) {
        return passwordLength + "/" + algorithm + "/" + secretKey + "/" + counter;
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