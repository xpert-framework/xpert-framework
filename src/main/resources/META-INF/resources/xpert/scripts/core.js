Xpert = {
    escapeClientId: function (id) {
        return "#" + id.replace(/:/g, "\\:");
    },
    closeModalMessages: function (widgetDialog, onconfirm) {
        widgetDialog.hide();
        if (onconfirm) {
            onconfirm();
        }

        //enable events outside modal
        $.each($(".faces-modal-messages"), function (key, value) {
            //verify if method $.off exists (for primefaces legacy)
            if(typeof $(document).off === "function"){
                $(document).off('focus.' + this.id + ' mousedown.' + this.id + ' mouseup.' + this.id + ' keydown.' + this.id);
            }
        });

        $('.faces-modal-messages').remove();
        $('div[id*=xpertModalMessages]').remove();
    },
    clearDownloadCookie: function () {
        $.cookie('xpert.download', null, {
            path: '/',
            expires: -1
        });
    },
    createOverlayWithDialog: function (id, message) {
        //create overlay
        var dialog = '<div class="ui-widget-overlay" style="z-index: 10000" id="' + id + '_overlay"></div>' +
                '<div class="overlay-download ui-widget" style="z-index: 10001" id="' + id + '"><div class="content"><p>' + message + '</p></div>';
        $(document.body).append(dialog);
        $("#" + id + "," + "#" + id + "_overlay").css({
            'width': $(document).width(),
            'height': $(document).height()
        });
    },
    removeOverlayWithDialog: function (id) {
        $("#" + id).remove();
        $("#" + id + "_overlay").remove();
    },
    highlightCode: function (name) {
        dp.SyntaxHighlighter.HighlightAll(name);
    },
    popupTextArea: function (selector) {
        var content = $(selector).html();
        var wnd = window.open('', '_blank', 'width=750, height=400, location=0, resizable=1, menubar=0, scrollbars=0');
        wnd.document.write('<textarea style="width:99%;height:99%">' + content + '</textarea>')
    },
    detailAuditTable: function (element) {
        $span = $(element).find(".ui-button-icon-left");
        if ($span.hasClass("ui-icon-plus")) {
            $span.removeClass("ui-icon-plus");
            $span.addClass("ui-icon-minusthick");
        } else {
            $span.removeClass("ui-icon-minusthick");
            $span.addClass("ui-icon-plus");
        }
        $(element).closest("tr").next("tr").toggle("fast");
    },
    skinButton: function (element) {
        $(element).mouseover(function () {
            $(this).addClass('ui-state-hover');
        }).mouseout(function () {
            $(this).removeClass('ui-state-active ui-state-hover');
        }).mousedown(function () {
            $(this).addClass('ui-state-active');
        }).mouseup(function () {
            $(this).removeClass('ui-state-active');
        }).focus(function () {
            $(this).addClass('ui-state-focus');
        }).blur(function () {
            $(this).removeClass('ui-state-focus');
        }).keydown(function (e) {
            if (e.keyCode == $.ui.keyCode.SPACE || e.keyCode == $.ui.keyCode.ENTER || e.keyCode == $.ui.keyCode.NUMPAD_ENTER) {
                $(this).addClass('ui-state-active');
            }
        }).keyup(function () {
            $(this).removeClass('ui-state-active');
        });

        return this;
    },
    initDateFilter: function (element) {
        var $column = $(Xpert.escapeClientId(element));
        var $dataTable = $column.closest('.ui-datatable');

        //has reflow datatable
        if ($dataTable.hasClass("ui-datatable-reflow")) {
            var $dataFilter = $column.find('.panel-calendar-filter');
            $dataFilter.appendTo($column);
        }

    },
    dateFilter: function (element) {
        var $column = $(Xpert.escapeClientId(element));
        var $inputFilter = $column.find('.ui-column-filter');

        var dateStart = $column.find('.calendar-filter-start input').val();
        var dateEnd = $column.find('.calendar-filter-end input').val();
        var concatDate = dateStart + " ## " + dateEnd;
        $inputFilter.val(concatDate);

    },
    clearDateFilter: function (element) {
        $(element).closest('th').find('input').val('');
    },
    refreshDateFilter: function (column, dateStart, dateEnd) {
        var $column = $(Xpert.escapeClientId(column));
        $column.find('.calendar-filter-start input').val(dateStart);
        $column.find('.calendar-filter-end input').val(dateEnd);
    },
    spreadCheckBoxList: function (id, columns, highlight) {
        var $table = $("input[id ^= " + (id.replace(/:/g, "\\:") + "]:first")).closest("table");
        var $td = $table.find("tr td");
        var inner = "";
        var total = 0;
        jQuery.each($td, function (i, element) {
            if (total == 0) {
                inner = inner + "<tr>";
            }
            var checkbox = $(element).find("input[type=checkbox],input[type=radio]");
            if (highlight && checkbox[0].checked == true) {
                inner = inner + "<td class='ui-state-highlight' style='border: 0;'>";
            } else {
                inner = inner + "<td>";
            }
            inner = inner + element.innerHTML + "</td>";
            if (total == columns - 1) {
                inner = inner + "</tr>";
                total = 0;
            } else {
                total++;
            }
        });
        $table.html(inner);
        if (highlight == true) {
            $table.find("input[type=checkbox],input[type=radio]").click(function () {
                var $td = $(this).closest("td");
                $td.css("border", 0);
                if ($(this).attr("type") == "radio") {
                    $table.find("td").removeClass("ui-state-highlight");
                }
                if (this.checked) {
                    $td.addClass("ui-state-highlight");
                } else {
                    $td.removeClass("ui-state-highlight");
                }
            });
        }
    },
    printPDF: function (element, target) {

        jQuery.fn.outerHtml = function () {
            return $($('<div></div>').html(this.clone())).html();
        };

        var $element = $(Xpert.escapeClientId(element));
        var htmlImports = "";
        var htmlBody = $(Xpert.escapeClientId(target)).clone().outerHtml();

        var htmlTarget = "";

        $("link").each(function () {
            htmlImports = htmlImports + "<link type='text/css' rel='stylesheet' href='" + $(this).attr("href") + "' />";
        });

        htmlTarget = "<html><head>" + htmlImports + "</head><body>" + htmlBody + "</body></html>";

        //add a hidden input in form
        var $form = $element.closest("form");
        //remove input
        $form.find("input[name=xpert_html_export]").remove();
        //add input
        $("<input>").attr("type", "hidden").attr("name", "xpert_html_export").attr("value", htmlTarget).appendTo($form);


    },
    disableTree: function (selector) {
        $(selector + " .ui-treenode-label").removeClass("ui-state-highlight");
        $(selector + " .ui-tree-selectable *:not(.ui-tree-toggler)").click(function () {
            return false;
        });
        $(selector + " .ui-chkbox-box").addClass("ui-state-disabled").click(function () {
            return false;
        });
    },
    disableTreeNode: function (selector) {
        $(selector + " > .ui-tree-selectable .ui-chkbox-box").removeClass("ui-state-highlight");
        $(selector + " > .ui-tree-selectable *:not(.ui-tree-toggler)").click(function () {
            return false;
        });
        $(selector + " > .ui-tree-selectable .ui-chkbox-box, " + selector + " > .ui-tree-selectable .ui-treenode-label").addClass("ui-state-disabled").click(function () {
            return false;
        });
    },
    fixColumnToggler: function () {
        var $element = $(".ui-columntoggler-item:contains($(function())");
        if ($element.length > 0) {
            var originalText = $element.text();
            //pt_br, es
            var indexOfFunction = originalText.indexOf("de$(function()");
            if (indexOfFunction < 0) {
                //en
                indexOfFunction = originalText.indexOf("from$(function()");
            }
            if (indexOfFunction > -1) {
                var newText = originalText.substring(0, indexOfFunction);
                $element.find("> label").text(newText);
            }
        }
    }
};
Xpert.behavior = {
    download: function (element, cfg) {
        if (cfg.onstart) {
            cfg.onstart();
        }
        var $object = $(Xpert.escapeClientId(element));
        Xpert.clearDownloadCookie();
        var token = $($object.closest("form")).find("input[id*=jakarta\\.faces\\.ViewState]").val();
        if (token == null || token == "") {
            //find by name
            token = $($object.closest("form")).find("input[name*=jakarta\\.faces\\.ViewState]").val();
            if (token == null || token == "") {
                return;
            }
        }
        if (cfg.showModal) {
            Xpert.createOverlayWithDialog("dialog-download", cfg.message);
        }
        var poll = setInterval(function () {
            var cookie = $.cookie('xpert.download');
            if (cookie != null && cookie.replace(/\"/g, "") == token.replace(/\"/g, "")) {
                if (cfg.oncomplete) {
                    cfg.oncomplete();
                }
                Xpert.clearDownloadCookie();
                if (cfg.showModal) {
                    Xpert.removeOverlayWithDialog("dialog-download");
                }
                clearInterval(poll);
            }
        }, 500);
    }

};