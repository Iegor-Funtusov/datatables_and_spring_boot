;(function () {

    dataTablesEasy = {};

    dataTablesEasy.config = {
        tableClass: 'datatables-easy'
    };

    dataTablesEasy.init = function () {
        $('table.' + dataTablesEasy.config.tableClass).each(function () {
            return configureDataTable(this)
        });
    };

    function configureDataTable(table) {
        var t = $(table);
        var pdContainer = getAttributeByPageDataContainer(t);
        if (!pdContainer) {
            console.log('Attribute with page data is not found on table ' + table);
            return false;
        }
        console.log(pdContainer);
        var columnDefs = buildColumnDefs(t);
        var orderInfo = initOrders(columnDefs, pdContainer);

        var totalElements = pdContainer.totalElements;
        var size = pdContainer.size;
        var displayStart = pdContainer.displayStart;
        var displayEnd = pdContainer.displayEnd;

        var dataTablesSettings = {
            colReorder: true,
            responsive: true,
            pageLength: size,
            pagingType: "full",
            order: [orderInfo.col, orderInfo.dir],
            columnDefs: columnDefs,
            displayStart: displayStart - 1,
            dom: '<"d-flex justify-content-between"B>t<"row"><"d-flex justify-content-between"<"mt-2"l>ip><"clear">',
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
        };

        renderFitlers(t, columnDefs, pdContainer);

        //configButtons(dataTablesSettings);

        var appDataTable = t
            .DataTable(dataTablesSettings)
            .on('order.dt', function (e, settings, order) {
                pdContainer.order = columnDefs[order[0].col].field + '_' + order[0].dir;
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

        overrideButtonColor();

        appDataTable.columns().every(function (i) {
            $('input', this.footer()).on('keypress', function (event) {
                if (event.keyCode === 13) {
                    subscribeEventAndRequest(this, pdContainer, columnDefs[i].field, this.value);
                }
            });
        });

        appDataTable.columns().every(function (i) {
            $('select', this.footer()).on('change', function () {
                var value = $.fn.dataTable.util.escapeRegex($(this).val());
                subscribeEventAndRequest(this, pdContainer, columnDefs[i].field, value);
            });
        });

        new $.fn.dataTable.FixedHeader(appDataTable, {
            headerOffset: 1
        });

        $('table.' + dataTablesEasy.config.tableClass + ' tbody').on('click', 'td.details-control', function () {
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

        if (false) {
            var drops;
            if ((displayEnd - displayStart + 1) < size) {
                drops = 'down';
            } else {
                drops = 'up';
            }
            var startPeriod;
            var endPeriod;
            appDataTable.columns().every(function (i) {
                var column = pdContainer.dataTablesInput.columns[i];
                if (column !== undefined) {
                    var createTime = "createTime";
                    var updateTime = "updateTime";
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
                                        // setColumnValueAndRunDataTableRequest(this, pdContainer, i, startPeriod + ':' + endPeriod);
                                    }
                                });
                            });
                        }
                    }
                }
            });
        }

        var tableForm = t.closest('form');
        if (!tableForm.length) {
            t.css('border-color', 'red');
            alert('Please wrap highlighted table with form');
        }
        // if (!tableForm.find('input[type="submit"][class="internal-submit"]')) {
        //     $('<input>').attr({
        //         hidden: 'true',
        //         type: 'submit',
        //         class: 'internal-submit'
        //     }).appendTo(tableForm);
        // }
    }

    function initOrders(columnDefs, pdContainer) {
        var orderColDir = pdContainer.order;
        var order = {};
        if (orderColDir !== null) {
            var sort = orderColDir.split('_');
            for (var index in columnDefs) {
                var dtInfo = columnDefs[index].field;
                var col = columnDefs[index].targets[0];
                if (dtInfo === sort[0]) {
                    order.col = col;
                    order.dir = sort[1];
                }
            }
        } else {
            order.col = 0;
            order.dir = "asc";
        }
        return order;
    }

    function renderFitlers(t, columnDefs, pdContainer) {
        var thead = t.find('thead');
        var tfoot = t.find('tfoot');
        if (tfoot.length === 0) {
            t.append($('<tfoot></tfoot>'));
            tfoot = t.find('tfoot');
        }
        thead.find('tr').clone(true).appendTo(tfoot);
        tfoot.find('tr>th').each(function (i) {
            var title = $(this).text();
            var field = columnDefs[i].field;
            var type = columnDefs[i].type;
            var enums = getAllEnums();

            if (pdContainer.filterMap !== null) {
                if (isString(type)) {
                    var filterMap = new Map();
                    filterMap.dict = pdContainer.filterMap;
                    var searchField = filterMap.get(field);
                    if (searchField !== undefined) {
                        $(this).html('<input type="text" class="form-control" value="' + searchField + '" />');
                    } else {
                        $(this).html('<input type="text" class="form-control" placeholder="Search ' + title + '" />');
                    }
                }
                if (isEnum(type)) {
                    if (enums.hasOwnProperty(field)) {
                        initEnums(this, enums[field]);
                    }
                }
            } else {
                if (isString(type)) {
                    $(this).html('<input type="text" class="form-control" placeholder="Search ' + title + '" />');
                }
                if (isEnum(type)) {
                    if (enums.hasOwnProperty(field)) {
                        initEnums(this, enums[field]);
                    }
                }
            }
        });
    }

    function isString(type) {
        return type === 'string';
    }

    function isEnum(type) {
        return type === 'enum';
    }

    function initEnums(ownerSelect, e) {
        var selectList = document.createElement("select");
        selectList.setAttribute('class', 'form-control');
        for (var val in e) {
            var option = document.createElement("option");
            option.value = e[val];
            option.text = e[val];
            selectList.appendChild(option);
        }
        ownerSelect.innerText = '';
        ownerSelect.appendChild(selectList);
    }

    function subscribeEventAndRequest(ownerEvent, pdContainer, field, value) {
        var filterMap = new Map();
        if (pdContainer.filterMap === null) {
            pdContainer.filterMap = new Map();
        } else {
            filterMap.dict = pdContainer.filterMap;
        }
        filterMap.put(field, value);
        pdContainer.filterMap = filterMap.dict;
        dataTableRequest(ownerEvent, pdContainer);
    }

    function configButtons(dataTablesSettings) {
        dataTablesSettings['buttons'] = [
            'colvis',
            'print',
            'copyHtml5',
            'csvHtml5',
            'excelHtml5',
            'pdfHtml5',
            {
                text: 'JSON',
                action: function (e, dt) {
                    var data = dt.buttons.exportData();
                    $.fn.dataTable.fileSave(
                        new Blob([JSON.stringify(data)]),
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
        ];
    }

    function overrideButtonColor() {
        var btns = document.querySelector('div.dt-buttons.btn-group');
        if (btns) {
            var kbButtons = btns.getElementsByTagName("button");
            for (var i = 0; i < kbButtons.length; i++) {
                kbButtons[i].style.backgroundColor = '#f8f9fa';
                kbButtons[i].style.color = 'black';
            }
        }
    }

    function getAttributeByPageDataContainer(table) {
        var pageDataJson = table.attr('dt-page');
        if (pageDataJson) {
            return eval('(' + pageDataJson + ')');
        }
        return null;
    }

    var DEFAULT_COLUMN_DEF = {
        orderable: true,
        searchable: true
    };

    function buildColumnDefs(table) {
        var columnDefs = [];
        var columnIndex = 0;
        table.find('thead>tr>th').each(function () {
            var cd = {};
            $.each(this.attributes, function () {
                if (this.specified) {
                    if (this.name.startsWith('dt-')) {
                        cd[this.name.substring(3)] = buildColumnAttributeValue(this.name, this.value);
                    }
                }
            });
            if (!cd.hasOwnProperty('orderable') && cd.field) {
                cd.orderable = true;
            }
            if (!cd.hasOwnProperty('searchable') && cd.field) {
                cd.searchable = true;
            }
            cd.targets = [columnIndex];
            columnDefs.push(cd);
            columnIndex++;
        });
        console.log(columnDefs);
        return columnDefs;
    }

    function buildColumnAttributeValue(name, value) {
        if (value === 'true' || value === 'false') {
            return eval(value);
        }
        if (name.endsWith('-json')) {
            return eval('(' + value + ')');
        }
        return value;
    }

    function dataTableRequest(owner, pdContainer) {
        pdContainer = JSON.stringify(pdContainer);

        console.log('dt = ' + pdContainer);

        $('<input>').attr({
            type: 'hidden',
            name: 'pageData',
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

}());
