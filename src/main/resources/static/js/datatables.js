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

$(document).ready(function () {

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
                            buttons: [
                                'colvis',
                                'copyHtml5',
                                'csvHtml5',
                                'excelHtml5',
                                'pdfHtml5',
                                'print'
                            ],
                            columnDefs: [{
                                "orderable": columnDefs.orderable,
                                "searchable": columnDefs.orderable,
                                "targets": columnDefs.targets
                            }],
                            displayStart: displayStart - 1,
                            dom: '<"dom_wrapper fh-fixedHeader"<"d-flex justify-content-between"Bl>>t<"d-flex justify-content-between"ip><"clear">',
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

                appDataTable.columns().every(function (i) {
                    $('input', this.footer()).on('keypress', function (event) {
                        if (event.keyCode === 13) {
                            pdContainer.dataTablesInput.columns[i].search.value = this.value;
                            pdContainer.dataTablesInput.columns[i].search.regex = true;
                            pdContainer.dataTablesInput.columnsAsMap = null;
                            dataTableRequest(this, pdContainer);
                        }
                    });
                });

                appDataTable.columns().every(function (i) {
                    $('select', this.footer()).on('change', function () {
                        pdContainer.dataTablesInput.columns[i].search.value = $.fn.dataTable.util.escapeRegex(
                            $(this).val()
                        );
                        pdContainer.dataTablesInput.columns[i].search.regex = true;
                        pdContainer.dataTablesInput.columnsAsMap = null;
                        dataTableRequest(this, pdContainer);
                    });
                });

                appDataTable.buttons().container()
                    .appendTo('table.app-data-table_wrapper .col-sm-6:eq(0)');


                new $.fn.dataTable.FixedHeader(appDataTable, {
                    headerOffset: 1,
                    footer: true,
                    footerOffset: 1
                });

                $('table.app-data-table tbody').on('click', 'td.details-control', function () {
                    var tr = $(this).closest('tr');
                    var row = appDataTable.row(tr);

                    if (row.child.isShown()) {
                        // This row is already open - close it
                        row.child.hide();
                        tr.removeClass('shown');
                    } else {
                        // Open this row
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
                        if (pdContainer.dataTablesInput.columns[i].data === createTime) {
                            var date = pdContainer.dataTablesInput.columns[i].search.value;
                            if (date !== '') {
                                date = date.replace(/ /gi, '').split('-');
                                var period = new Date(date[0]);
                                startPeriod = moment(period);
                                period = new Date(date[1]);
                                endPeriod = moment(period);
                            }
                        }
                    }
                });

                if (startPeriod !== undefined && endPeriod !== undefined) {
                    var daterangepicker = $('input[name=createTime]').daterangepicker({
                        drops: drops,
                        startDate: startPeriod,
                        endDate: endPeriod,
                        alwaysShowCalendars: true,
                        ranges: {
                            'All':[startPeriod, moment()],
                            'Today': [moment(), moment()],
                            'Yesterday': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
                            'Last 7 Days': [moment().subtract(6, 'days'), moment()],
                            'Last 30 Days': [moment().subtract(29, 'days'), moment()],
                            'This Month': [moment().startOf('month'), moment().endOf('month')],
                            'Last Month': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
                        }
                    }, function (start, end) {
                        startPeriod = start;
                        endPeriod = end;
                    });

                    appDataTable.columns().every(function (i) {
                        $('input', this.footer()).on('change', function () {
                            if (pdContainer.dataTablesInput.columns[i].data === createTime && daterangepicker !== undefined) {
                                pdContainer.dataTablesInput.columns[i].search.value = startPeriod + ':' + endPeriod;
                                pdContainer.dataTablesInput.columns[i].search.regex = true;
                                pdContainer.dataTablesInput.columnsAsMap = null;
                                dataTableRequest(this, pdContainer);
                            }
                        });
                    });
                }

            });
});