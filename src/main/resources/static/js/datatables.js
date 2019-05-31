;(function() {

    dataTableProcess = {};

    dataTableProcess.init = function () {

        $(function () {
            $('table.app-data-table').colResizable();
        });

        $('table.app-data-table')
            .each(
                function () {
                    var pdContainer = getAttributeByPageDataContainer(this);
                    var totalElements = pdContainer.totalElements;
                    var size = pdContainer.size;
                    var displayStart = pdContainer.displayStart;
                    var displayEnd = pdContainer.displayEnd;
                    var columnDefs = pdContainer.columnDefs;
                    var orderCol = pdContainer.orderCol;
                    var orderDir = pdContainer.orderDir;
                    var createTime = "createTime";
                    var updateTime = "updateTime";

                    var appDataTable = $(this)
                        .DataTable(
                            {
                                colReorder: true,
                                responsive: true,
                                pageLength: size,
                                pagingType: "full",
                                order: [orderCol, orderDir],
                                columnDefs: [{
                                    "orderable": columnDefs.orderable,
                                    "searchable": columnDefs.orderable,
                                    "targets": columnDefs.targets
                                }],
                                buttons: [
                                    'colvis',
                                    'print',
                                    'copyHtml5',
                                    'csvHtml5',
                                    'excelHtml5',
                                    'pdfHtml5',
                                    {
                                        text: 'JSON',
                                        action: function ( e, dt ) {
                                            var data = dt.buttons.exportData();
                                            $.fn.dataTable.fileSave(
                                                new Blob( [ JSON.stringify( data ) ] ),
                                                'export_data.json'
                                            );
                                        }
                                    },
                                    {
                                        text: 'Clear',
                                        action: function () {
                                            window.location.replace(window.location.href);
                                        }
                                    }
                                ],
                                displayStart: displayStart - 1,
                                dom: '<"d-flex justify-content-between"Bl>t<"d-flex justify-content-between"ip><"clear">',
                                preDrawCallback: function (settings) {
                                    settings.oFeatures.bServerSide = "ssp";
                                    settings.bDestroying = true;
                                    settings.fnDisplayEnd = function () {
                                        return displayEnd;
                                    };
                                    settings.fnRecordsTotal = function () {
                                        return totalElements;
                                    };
                                    settings.fnRecordsDisplay = function () {
                                        return totalElements;
                                    };
                                }
                            })
                        .on('order.dt', function (e, settings, order) {
                            pdContainer.orderCol = order[0].col;
                            pdContainer.orderDir = order[0].dir;
                            dataTableRequest(this, pdContainer);
                        })
                        .on('length.dt', function (e, settings, len) {
                            pdContainer.size = len;
                            dataTableRequest(this, pdContainer);
                        })
                        .on('page.dt', function () {
                            pdContainer.page = appDataTable.page.info().page + 1;
                            dataTableRequest(this, pdContainer);
                        });

                    overrideButtons();

                    appDataTable.columns().every(function (i) {
                        $('input', this.footer()).on('keypress', function (event) {
                            if (event.keyCode === 13) {
                                setColumnValueAndRunDataTableRequest(this, pdContainer, i, this.value);
                            }
                        });
                    });

                    appDataTable.columns().every(function (i) {
                        $('select', this.footer()).on('change', function () {
                            var value = $.fn.dataTable.util.escapeRegex(
                                $(this).val()
                            );
                            setColumnValueAndRunDataTableRequest(this, pdContainer, i, value);
                        });
                    });

                    new $.fn.dataTable.FixedHeader(appDataTable, {
                        headerOffset: 1
                    });

                    $('table.app-data-table tbody').on('click', 'td.details-control', function () {
                        var tr = $(this).closest('tr');
                        var row = appDataTable.row(tr);

                        if (row.child.isShown()) {
                            row.child.hide();
                            tr.removeClass('shown');
                        } else {
                            row.child(format(row.data())).show();
                            tr.addClass('shown');
                        }
                    });

                    var startPeriod;
                    var endPeriod;
                    var drops;
                    if ((displayEnd - displayStart + 1) < size) {
                        drops = 'down';
                    } else {
                        drops = 'up';
                    }

                    appDataTable.columns().every(function (i) {
                        var column = pdContainer.dataTablesInput.columns[i];
                        if (column !== undefined) {
                            var columnData = pdContainer.dataTablesInput.columns[i].data;
                            if (columnData === createTime || columnData === updateTime) {
                                var date = pdContainer.dataTablesInput.columns[i].search.value;
                                date = date.replace(/ /gi, '').split('-');
                                var period = new Date(date[0]);
                                startPeriod = moment(period);
                                period = new Date(date[1]);
                                endPeriod = moment(period);

                                if (startPeriod !== undefined && endPeriod !== undefined) {

                                    var dtTime = document.getElementById(columnData);

                                    var daterangepicker = jQuery(dtTime).daterangepicker({
                                        drops: drops,
                                        startDate: startPeriod,
                                        endDate: endPeriod,
                                        alwaysShowCalendars: true,
                                        ranges: initRanges(startPeriod)
                                    }, function (start, end) {
                                        startPeriod = start;
                                        endPeriod = end;
                                    });

                                    appDataTable.columns().every(function (i) {
                                        $('input', this.footer()).on('change', function () {
                                            if (daterangepicker !== undefined) {
                                                setColumnValueAndRunDataTableRequest(this, pdContainer, i, startPeriod + ':' + endPeriod);
                                            }
                                        });
                                    });
                                }
                            }
                        }
                    });
                });
    };

    function overrideButtons() {
        var btns = document.querySelector('div.dt-buttons.btn-group');
        var kbButtons = btns.getElementsByTagName("button");
        for (var i = 0; i < kbButtons.length; i++) {
            kbButtons[i].style.backgroundColor = '#f8f9fa';
            kbButtons[i].style.color = 'black';
        }
    }

    function clearrAll(pdContainer) {
        console.log('clear')
    }

    function getAttributeByPageDataContainer(owner) {
        var pageDataJson = owner.getAttribute('page-data-container');
        return eval('(' + pageDataJson + ')');
    }

    function dataTableRequest(owner, pdContainer) {
        pdContainer = JSON.stringify(pdContainer);
        $('<input>').attr({
            type: 'hidden',
            name: 'datatable',
            value: pdContainer
        }).appendTo(owner);

        var button = document.createElement("button");
        button.setAttribute('type', 'submit');
        button.style.visibility = 'hidden';
        owner.parentElement.appendChild(button);
        jQuery(button).trigger("click");
    }

    function initRanges(start) {
        return {
            'All': [start, moment()],
            'Today': [moment(), moment()],
            'Yesterday': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
            'Last 7 Days': [moment().subtract(6, 'days'), moment()],
            'Last 30 Days': [moment().subtract(29, 'days'), moment()],
            'This Month': [moment().startOf('month'), moment().endOf('month')],
            'Last Month': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
        };
    }

    function setColumnValue(pdContainer, i, value) {
        pdContainer.dataTablesInput.columns[i].search.value = value;
        pdContainer.dataTablesInput.columns[i].search.regex = true;
        pdContainer.dataTablesInput.columnsAsMap = null;
    }

    function setColumnValueAndRunDataTableRequest(thisVal, pdContainer, i, value) {
        setColumnValue(pdContainer, i, value);
        dataTableRequest(thisVal, pdContainer);
    }

} ());
